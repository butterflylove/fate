package destiny.fate.common.config;

/**
 * Created by zhangtianlong01 on 2017/9/21.
 */
public interface ServerConfig {

    /******* 前端配置 **********/

    int BACKEND_INIT_SIZE = 10;

    int BACKEND_MAX_SIZE = 20;

    int SERVER_PORT = 8888;

    String DEFAULT_CHARSET = "utf8";

    /******** 后端数据库配置  *******/
    String USER_NAME = "root";

    String PASS_WORD = "123456";

    String DATABASE = "test";

    String MySQL_HOST = "127.0.0.1";

    int MySQL_PORT = 3306;

    int BACKEND_INITIAL_WAIT_TIME = 60;

    int BACKEND_CONNECT_RETRY_TIMES = 3;

}
