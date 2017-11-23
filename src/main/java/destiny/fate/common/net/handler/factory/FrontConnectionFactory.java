package destiny.fate.common.net.handler.factory;

import destiny.fate.common.config.ServerConfig;
import destiny.fate.common.net.handler.backend.pool.MySqlDataSource;
import destiny.fate.common.net.handler.frontend.FrontendConnection;
import destiny.fate.common.net.handler.frontend.ServerQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * FrontendConnection工厂类
 * @author zhangtianlong
 */
public class FrontConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(FrontConnectionFactory.class);

    private final MySqlDataSource dataSource;

    public FrontConnectionFactory(MySqlDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 线程ID生成器
     */
    private static final AtomicInteger ACCEPT_ID_GENERATOR = new AtomicInteger(0);

    public FrontendConnection getConnection() {
        // TODO
        FrontendConnection connection = new FrontendConnection();
        connection.setDataSource(dataSource);
        connection.setQueryHandler(new ServerQueryHandler(connection));
        connection.setId(ACCEPT_ID_GENERATOR.getAndIncrement());
        logger.info("connection id={}", connection.getId());
        connection.setCharset(ServerConfig.DEFAULT_CHARSET);
        return connection;
    }
}
