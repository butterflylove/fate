package destiny.fate.route;

import destiny.fate.common.net.route.RouteResultset;
import destiny.fate.common.net.route.RouteResultsetNode;
import destiny.fate.parser.ServerParser;

/**
 * @author zhangtianlong
 */
public class FateStatementParser {

    public static RouteResultset parser(String sql, int sqlType) {
        switch (sqlType) {
            case ServerParser.SELECT:
                return null;
        }
        return null;
    }

    public static RouteResultset multiParse(String sql) {
        RouteResultset routeResultset = new RouteResultset();
        RouteResultsetNode[] nodes = new RouteResultsetNode[1];
        for (int i = 0; i < 1; i++) {
            RouteResultsetNode node = new RouteResultsetNode(String.valueOf(1), sql, ServerParser.SELECT);
            nodes[i] = node;
        }
        routeResultset.setNodes(nodes);
        return routeResultset;
    }
}
