package destiny.fate.common.net.route;

/**
 * @author zhangtianlong
 */
public class RouteResultsetNode {
    /**
     * 数据节点名称
     */
    private String name;

    /**
     * 执行的语句
     */
    private String statement;

    private int sqlType;

    public RouteResultsetNode(String name, String statement, int sqlType) {
        this.name = name;
        this.statement = statement;
        this.sqlType = sqlType;
    }

    public String getName() {
        return name;
    }

    public String getStatement() {
        return statement;
    }

    public int getSqlType() {
        return sqlType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteResultsetNode that = (RouteResultsetNode) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
