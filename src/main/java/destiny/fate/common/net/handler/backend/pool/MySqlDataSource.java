package destiny.fate.common.net.handler.backend.pool;

import destiny.fate.common.net.handler.backend.BackendConnection;

/**
 * Created by zhangtianlong01 on 2017/10/9.
 */
public class MySqlDataSource {

    private MySqlDataPool dataPool;

    public MySqlDataSource(MySqlDataPool dataPool) {
        this.dataPool = dataPool;
    }

    public BackendConnection getBackend() {
        return dataPool.getBackend();
    }
}
