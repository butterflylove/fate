package destiny.fate.common.pool;

import java.util.concurrent.Executor;

/**
 * @author zhangtianlong
 */
public interface ThreadPool {

    /**
     * 获取默认配置的线程池
     */
    Executor getExecutor(String name);

    /**
     * 
     */
}
