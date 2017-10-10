package destiny.fate.common.net.protocol;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public interface Versions {

    /** 协议版本 */
    byte PROTOCOL_VERSION = 10;

    /** 服务器版本 */
    byte[] SERVER_VERSION = "5.1.1-Fate".getBytes();
}
