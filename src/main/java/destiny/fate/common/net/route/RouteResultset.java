package destiny.fate.common.net.route;

/**
 * 路由结果集
 *
 * @author  zhangtianlong
 */
public class RouteResultset {
    /**
     * 原始查询语句
     */
    private String statement;

    private int sqlType;

    /**
     * 路由结果节点集
     */
    private RouteResultsetNode[] nodes;

    public int getNodeCount() {
        if (nodes == null) {
            return 0;
        } else {
            return nodes.length;
        }
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public RouteResultsetNode[] getNodes() {
        return nodes;
    }

    public void setNodes(RouteResultsetNode[] nodes) {
        this.nodes = nodes;
    }
}
