package destiny.fate.common.net.handler.frontend;

import destiny.fate.parser.ServerParseSelect;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public class SelectHandler {

    public static void handle(String stmt, FrontendConnection c, int offs) {
        int offset = offs;
        switch (ServerParseSelect.parse(stmt, offset)) {
            case ServerParseSelect.VERSION_COMMENT:
                SelectVersionComment.response(c);
                break;
        }
    }
}
