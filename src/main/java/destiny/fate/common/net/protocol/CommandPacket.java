package destiny.fate.common.net.protocol;

/**
 * Created by zhangtianlong on 17/11/20.
 */
public class CommandPacket extends MySQLPacket {

    public byte command;
    public byte[] arg;

    public void read(byte[] data) {

    }

    @Override
    public int calcPacketSize() {
        return 1 + arg.length;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Command Packet";
    }
}
