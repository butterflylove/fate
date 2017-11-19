package destiny.fate.common.net.handler.session;

import destiny.fate.common.net.handler.frontend.FrontendConnection;

/**
 * @author zhangtianlong
 */
public interface Session {

    /**
     * 获得前端连接
     */
    FrontendConnection getSource();

    /**
     * 获取当前对应的目标后端数量
     */
    int getTargetCount();

    /**
     * 开启一个会话执行
     */
    void execute(String sql, int type);

    /**
     * 提交一个会话
     */
    void commit();

    /**
     * 回滚一个会话
     */
    void rollback();

    /**
     * 取消一个正在执行的会话
     */
    void cancel(FrontendConnection sponsor);

    /**
     * 终止会话,必须在关闭源端连接后执行此方法;
     */
    void terminate();

    /**
     * 关闭会话
     */
    void close();

}
