package destiny.fate.common.net.handler.backend.cmd;

/**
 * Created by zhangtianlong01 on 2017/11/21.
 */
public enum CmdType {
    /**
     * 前端连接发起的命令
     */
    FRONTEND_TYPE("0"),

    /**
     * 后端发起的命令
     */
    BACKEND_TYPE("1");

    private String code;

    CmdType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
