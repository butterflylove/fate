package destiny.fate.common.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangtianlong01 on 2017/9/21.
 */
public class FateServer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FateServer.class);

    @Override
    public void run() {
        logger.info("Start the MySQL Sharding Proxy");
    }

    public static void main(String[] args) {

    }
}
