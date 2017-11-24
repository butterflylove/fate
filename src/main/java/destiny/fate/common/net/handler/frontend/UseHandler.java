package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.protocol.util.ErrorCode;

/**
 * USE操作的handler
 *
 * @author zhangtianlong
 */
public class UseHandler {

    public static void handle(String sql, FrontendConnection c, int offset) {
        String schema = sql.substring(offset).trim();
        int length = schema.length();
        if (length > 0) {
            if (schema.charAt(0) == '`' && schema.charAt(length - 1) == '`') {
                schema = schema.substring(1, length - 1);
            }
        }

        // 当前连接已经指定schema
        if (c.getSchema() != null) {
            if (c.getSchema().equals(schema)) {
                c.writeOk();
            } else {
                // 切换数据库
                c.setSchema(schema);
                c.writeOk();
                return;
            }
        }
        c.setSchema(schema);
        c.writeOk();
    }

    public static void main(String[] args) {
        String x = "`test`";
        System.out.println(x.substring(1, x.length() - 2));
    }
}
