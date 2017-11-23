package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.response.SelectDatabase;
import destiny.fate.common.net.response.SelectVersionComment;
import destiny.fate.parser.ServerParseSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理SELECT操作的handler
 *
 * @author zhangtianlong
 */
public class SelectHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectHandler.class);

    public static void handle(String stmt, FrontendConnection c, int offs) {
        int offset = offs;
        switch (ServerParseSelect.parse(stmt, offset)) {
            case ServerParseSelect.VERSION_COMMENT:
                logger.info("VERSION_COMMENT==========");
                SelectVersionComment.response(c);
                break;
            case ServerParseSelect.DATABASE:
                logger.info("SELECT DATABASE()======");
                SelectDatabase.response(c);
                break;
        }
    }
}
