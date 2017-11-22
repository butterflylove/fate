package destiny.fate.common.net.handler.backend.pool;

import destiny.fate.common.config.ServerConfig;
import destiny.fate.common.config.SocketConfig;
import destiny.fate.common.net.exception.RetryConnectFailException;
import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.BackendHeadHandler;
import destiny.fate.common.net.handler.factory.BackendConnectionFactory;
import destiny.fate.common.net.handler.factory.BackendHandlerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MySQL 连接池
 */
public class MySqlDataPool {

    private static final Logger logger = LoggerFactory.getLogger(MySqlDataPool.class);

    /**
     * 当前连接池中空闲的连接数
     */
    private int idleCount;

    /**
     * 最大连接数
     */
    private final int maxPoolSize;

    /**
     * 初始化连接数
     */
    private int initSize;

    /**
     * 连接池
     */
    private final BackendConnection[] items;

    /**
     * Backend Connection Factory
     */
    private BackendConnectionFactory factory;

    /**
     * Backend Event Loop Group
     */
    private EventLoopGroup backendGroup;

    /**
     * Backend BootStrap
     */
    private Bootstrap b;

    /**
     * 线程间同步的锁
     */
    private CountDownLatch latch;

    /**
     * get/put操作的锁
     */
    private final ReentrantLock lock;

    /**
     * 当前连接池是否成功初始化的标记
     */
    private final AtomicBoolean initialized;

    /**
     * DataPool的Command Allocator
     */
    private ByteBufAllocator allocator;


    public MySqlDataPool(int initSize, int maxPoolSize) {
        this.initSize = initSize;
        this.maxPoolSize = maxPoolSize;
        this.idleCount = 0;
        this.items = new BackendConnection[maxPoolSize];
        this.backendGroup = new NioEventLoopGroup();
        this.b = new Bootstrap();
        this.latch = new CountDownLatch(initSize);
        this.lock = new ReentrantLock();
        this.initialized = new AtomicBoolean(false);
        this.allocator = new UnpooledByteBufAllocator(false);
    }

    public void init() {
        factory = new BackendConnectionFactory(this);
        // 使用PooledBuf来减少GC
        b.group(backendGroup).channel(NioSocketChannel.class).handler(new BackendHandlerInitializer(factory))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        setOption(b);
        initBackends();
        markInitialized();
    }

    private void setOption(Bootstrap bootstrap) {
        bootstrap.option(ChannelOption.SO_RCVBUF, SocketConfig.BACKEND_SOCKET_RECV_BUF)
                .option(ChannelOption.SO_SNDBUF, SocketConfig.BACKEND_SOCKET_SEND_BUF)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, SocketConfig.CONNECT_TIMEOUT_MILLIS)
                .option(ChannelOption.SO_TIMEOUT, SocketConfig.SO_TIMEOUT)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1024 * 1024);

    }

    /**
     * 初始化后端连接
     */
    private void initBackends() {
        List<ChannelFuture> futureList = new ArrayList<ChannelFuture>();
        for (int i = 0; i < initSize; i++) {
            ChannelFuture future = b.connect(ServerConfig.MySQL_HOST, ServerConfig.MySQL_PORT);
            futureList.add(future);
        }
        try {
            latch.await(ServerConfig.BACKEND_INITIAL_WAIT_TIME, TimeUnit.SECONDS);

            // 后端初始化完成
            for (ChannelFuture future : futureList) {
                //future.sync();
                recycle(getInitBackendConnFromFuture(future));
            }
            latch = null;
            logger.info("data pool start up.");
        } catch (Exception e) {
            logger.error("latch fail", e);
        }
    }

    public void recycle(BackendConnection conn) {
        putBackend(conn);
    }

    public void putBackend(BackendConnection conn) {
        lock.lock();
        try {
            if (conn.isAlive()) {
                if (idleCount < maxPoolSize) {
                    items[idleCount] = conn;
                    idleCount++;
                } else {
                    conn.close();
                    logger.info("backend connection too much, so close it.");
                }
            } else {
                logger.info("backend connection is not alive, so discard it.");
            }
        } finally {
            lock.unlock();
        }
    }

    private void markInitialized() {
        initialized.compareAndSet(false, true);
    }

    public BackendConnection getBackend() {
        BackendConnection backend = null;
        lock.lock();
        try {
            // idleCount初始为0
            if (idleCount >= 1 && items[idleCount-1] != null) {
                backend = items[idleCount-1];
                idleCount--;
                return backend;
            }
        } finally {
            lock.unlock();
        }
        // create new connection
        logger.info("create new connection");
        backend = createNewConnection();
        return backend;
    }

    private BackendConnection createNewConnection() {
        for (int i = 0; i < ServerConfig.BACKEND_CONNECT_RETRY_TIMES; i++) {
            ChannelFuture future = b.connect(ServerConfig.MySQL_HOST, ServerConfig.MySQL_PORT);
            BackendConnection backend = getBackendConnFromFutrue(future);
            if (backend != null) {
                return backend;
            }
        }
        throw new RetryConnectFailException("Retry Connect Error Host:" + ServerConfig.MySQL_HOST
                + " Port:" + ServerConfig.MySQL_PORT);
    }

    private BackendConnection getBackendConnFromFutrue(ChannelFuture future) {
        try {
            future.sync();
            BackendHeadHandler firstHandler = (BackendHeadHandler) future.channel().pipeline()
                    .get(BackendHeadHandler.HANDLER_NAME);
            // TODO
            //firstHandler.getSource().syncLatch.await();
            return firstHandler.getSource();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BackendConnection getInitBackendConnFromFuture(ChannelFuture future) {
        try {
            future.sync();
            BackendHeadHandler firstHandler =
                    (BackendHeadHandler) future.channel().pipeline().get(BackendHeadHandler.HANDLER_NAME);
            return firstHandler.getSource();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        AtomicBoolean x = new AtomicBoolean(false);
        x.set(true);
        x.compareAndSet(false, false);
        System.out.println(x);
    }
}
