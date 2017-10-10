package destiny.fate.common.net.handler.frontend;

import destiny.fate.parser.ServerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public class ServerQueryHandler implements FrontendQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerQueryHandler.class);

    private FrontendConnection source;

    public ServerQueryHandler(FrontendConnection source) {
        this.source = source;
    }

    @Override
    public void query(String sql) {
        logger.info("SQL = {}", sql);

        int rs = ServerParser.parse(sql);
        switch (rs & 0xff) {
            case ServerParser.SELECT:
                SelectHandler.handle(sql, source, rs >>> 8);
                break;
        }
    }
}
