package destiny.fate.common.net.protocol;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public class OkPacket {
    public static final byte FIELD_COUNT = 0x00;

    public static final byte[] OK = new byte[] { 7, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0 };
    public static final byte[] AUTH_OK = new byte[] { 7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0 };
}
