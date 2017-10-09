package destiny.fate.common.net.handler.factory;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.pool.MySqlDataPool;

/**
 * Created by zhangtianlong01 on 2017/10/9.
 */
public class BackendConnectionFactory {

    private MySqlDataPool mySqlDataPool;

    public BackendConnectionFactory(MySqlDataPool mySqlDataPool) {
        this.mySqlDataPool = mySqlDataPool;
    }

    public BackendConnection getConnection() {
        BackendConnection connection = new BackendConnection(mySqlDataPool);
        return connection;
    }
}
