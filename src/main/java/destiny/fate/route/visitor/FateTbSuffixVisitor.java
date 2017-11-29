package destiny.fate.route.visitor;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

/**
 * Created by zhangtianlong01 on 2017/11/29.
 */
public class FateTbSuffixVisitor extends MySqlOutputVisitor {

    private String tableSuffix = null;

    public FateTbSuffixVisitor(Appendable appender, String tableSuffix) {
        super(appender);
        this.tableSuffix = tableSuffix;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        return super.visit(x);
    }
}
