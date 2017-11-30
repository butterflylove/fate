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
        // 根据最后一个字节的值来判断SQL语句的类型
        switch (rs & 0xff) {
            case ServerParser.SELECT:
                logger.info("execute select");
                SelectHandler.handle(sql, source, rs >>> 8);
                break;
            case ServerParser.USE:
                logger.info("execute use");
                UseHandler.handle(sql, source, rs >>> 8);
                break;
            case ServerParser.SHOW:
                // TODO 支持逻辑上的数据源
                logger.info("execute show");
                // 无符号左移动8位获取原来SQL字符移动的位数
                ShowHandler.handle(sql, source, rs >>> 8);
                break;
            default:
                logger.info("default execute");
                source.execute(sql, rs);
        }
    }
}
