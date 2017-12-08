package destiny.fate.route;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import destiny.fate.common.net.route.RouteResultset;
import destiny.fate.common.net.route.RouteResultsetNode;
import destiny.fate.parser.ServerParser;
import destiny.fate.route.visitor.FateTbSuffixVisitor;
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
        logger.info("default parse");
        return multiParse(sql, sqlType);
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
        for (int i = 1; i <= tbCount; i++) {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            SQLSelectStatement selectStatement = parser.parseSelect();
            StringBuilder builder = new StringBuilder();
            FateTbSuffixVisitor suffixVisitor = new FateTbSuffixVisitor(builder, "_" + i);
            selectStatement.accept(suffixVisitor);
            RouteResultsetNode node = new RouteResultsetNode(String.valueOf(i), builder.toString(), ServerParser.SELECT);
            nodes[i - 1] = node;
        }
        routeResultset.setNodes(nodes);
        return routeResultset;
    }

    public static void main(String[] args) {
        String sql = "SELECT * FROM dist";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSelectStatement selectStmt = parser.parseSelect();
        StringBuilder builder = new StringBuilder();
        FateTbSuffixVisitor visitor = new FateTbSuffixVisitor(builder, "_" + 1);
        selectStmt.accept(visitor);
        System.out.println(builder.toString());
    }
}
