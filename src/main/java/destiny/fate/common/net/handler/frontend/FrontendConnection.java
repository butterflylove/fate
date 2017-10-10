package destiny.fate.common.net.handler.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangtianlong01 on 2017/10/9.
 */
public class FrontendConnection extends AbstractFrontendConnection {

    private static final Logger logger = LoggerFactory.getLogger(FrontendConnection.class);

    private long id;

    protected String host;

    protected int port;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void close() {
        logger.info("close frontend connection, host:{}, port:{}", host, port);
        // TODO
    }
}
