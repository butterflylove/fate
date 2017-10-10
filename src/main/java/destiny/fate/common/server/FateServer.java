package destiny.fate.common.server;

import destiny.fate.common.config.ServerConfig;
import destiny.fate.common.config.SocketConfig;
import destiny.fate.common.net.handler.backend.pool.MySqlDataPool;
import destiny.fate.common.net.handler.backend.pool.MySqlDataSource;
import destiny.fate.common.net.handler.factory.FrontHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fate Server启动器
 * @author zhangtianlong
 */
public class FateServer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FateServer.class);

    @Override
    public void run() {
        logger.info("Start the MySQL Sharding Proxy");
        startServer();
    }

    public static void main(String[] args) {
        FateServer fate = new FateServer();
        try {
            fate.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        // acceptor
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // worker
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            MySqlDataPool dataPool = new MySqlDataPool(ServerConfig.BACKEND_INIT_SIZE, ServerConfig.BACKEND_MAX_SIZE);
            dataPool.init();
            MySqlDataSource dataSource = new MySqlDataSource(dataPool);

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new FrontHandlerInitializer(dataSource))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, SocketConfig.CONNECT_TIMEOUT_MILLIS)
                .option(ChannelOption.SO_TIMEOUT, SocketConfig.SO_TIMEOUT);

            ChannelFuture future = bootstrap.bind(ServerConfig.SERVER_PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("端口{} 监听失败", ServerConfig.SERVER_PORT, e);
        }

    }
}
