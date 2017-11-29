package destiny.fate.route;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import destiny.fate.common.net.route.RouteResultset;
import destiny.fate.common.net.route.RouteResultsetNode;
import destiny.fate.parser.ServerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhangtianlong
 */
public class FateStatementParser {

    private static final Logger logger = LoggerFactory.getLogger(FateStatementParser.class);

    public static final int tbCount = 3;

    public static RouteResultset parser(String sql, int sqlType) {
        switch (sqlType) {
            case ServerParser.SELECT:
                logger.info("parse into select.");
                return singleParse(sql, ServerParser.SELECT);
            case ServerParser.DELETE:
                logger.info("parse into delete");
                return singleParse(sql, ServerParser.DELETE);
            case ServerParser.UPDATE:
                logger.info("parse into update");
                return singleParse(sql, ServerParser.UPDATE);
            case ServerParser.INSERT:
                logger.info("parse into insert");
                return singleParse(sql, ServerParser.INSERT);
        }
        return singleParse(sql, ServerParser.SELECT);
    }

    public static RouteResultset singleParse(String sql, int sqlType) {
        RouteResultset routeResultset = new RouteResultset();
        RouteResultsetNode[] nodes = new RouteResultsetNode[1];
        for (int i = 0; i < 1; i++) {
            RouteResultsetNode node = new RouteResultsetNode(String.valueOf(1), sql, sqlType);
            nodes[i] = node;
        }
        routeResultset.setNodes(nodes);
        return routeResultset;
    }

    public static RouteResultset multiParse(String sql, int sqlType) {
        RouteResultset routeResultset = new RouteResultset();
        RouteResultsetNode[] nodes = new RouteResultsetNode[tbCount];
        for (int i = 0; i <= tbCount; i++) {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            SQLSelectStatement selectStatement = parser.parseSelect();
            StringBuilder builder = new StringBuilder();

        }
        return null;
    }
}
