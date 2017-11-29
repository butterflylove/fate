package destiny.fate.route;

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

    public static RouteResultset parser(String sql, int sqlType) {
        switch (sqlType) {
            case ServerParser.SELECT:
                logger.info("parse into select.");
                return multiParse(sql, ServerParser.SELECT);
            case ServerParser.DELETE:
                logger.info("parse into delete");
                return multiParse(sql, ServerParser.DELETE);
            case ServerParser.UPDATE:
                logger.info("parse into update");
                return multiParse(sql, ServerParser.UPDATE);
            case ServerParser.INSERT:
                logger.info("parse into insert");
                return multiParse(sql, ServerParser.INSERT);
        }
        return multiParse(sql, ServerParser.SELECT);
    }

    public static RouteResultset multiParse(String sql, int sqlType) {
        RouteResultset routeResultset = new RouteResultset();
        RouteResultsetNode[] nodes = new RouteResultsetNode[1];
        for (int i = 0; i < 1; i++) {
            RouteResultsetNode node = new RouteResultsetNode(String.valueOf(1), sql, sqlType);
            nodes[i] = node;
        }
        routeResultset.setNodes(nodes);
        return routeResultset;
    }
}
