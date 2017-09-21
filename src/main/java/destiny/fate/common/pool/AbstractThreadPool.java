package destiny.fate.common.pool;

import java.util.concurrent.Executor;

/**
 * Created by zhangtianlong01 on 2017/9/21.
 */
public abstract class AbstractThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(String name) {
        return null;
    }
}
