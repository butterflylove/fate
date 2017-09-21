package destiny.fate.common.pool;

/**
 * 线程配置工厂
 * @author zhangtianlong
 */
public class ThreadPoolConfigFactory {

    /**
     * 默认配置
     */
    public static ThreadPoolConfig getDefaultConfig(String name) {
        ThreadPoolConfig config = new ThreadPoolConfig();
        config.setName(name);
        config.setCoreSize(16);
        config.setMaxSize(32);
        config.setMaxQueueSize(128);
        config.setKeepAliveTime(60L);
        return config;
    }
}
