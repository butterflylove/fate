package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.response.ShowDatabases;
import destiny.fate.parser.ServerParseShow;
import destiny.fate.parser.ServerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SHOW操作的handler
 *
 * @author zhangtianlong
 */
public final class ShowHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShowHandler.class);

    public static void handle(String stmt, FrontendConnection c, int offset) {
        int flag = ServerParseShow.parse(stmt, offset);
        logger.info("flag={}", flag);
        switch (flag) {
            case ServerParseShow.DATABASES:
                logger.info("show databases; ============");
                ShowDatabases.response(c);
                break;
            default:
                // show tables 会进入到这个分支
                logger.info("enter default show---------");
                c.execute(stmt, ServerParser.SHOW);
        }
    }
}
