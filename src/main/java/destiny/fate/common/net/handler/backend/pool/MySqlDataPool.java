package destiny.fate.common.net.handler.backend.pool;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.factory.BackendConnectionFactory;
import destiny.fate.common.net.handler.factory.BackendHandlerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
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
    }
}
