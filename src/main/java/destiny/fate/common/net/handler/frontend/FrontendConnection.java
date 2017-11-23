package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.pool.MySqlDataSource;
import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.ErrorPacket;
import destiny.fate.common.net.protocol.MySQLMessage;
import destiny.fate.common.net.protocol.OkPacket;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhangtianlong01 on 2017/10/9.
 */
public class FrontendConnection extends AbstractFrontendConnection {

    private static final Logger logger = LoggerFactory.getLogger(FrontendConnection.class);

    private long id;
    protected String user;
    protected String host;
    protected int port;
    protected String schema;
    protected String charset;
    protected int charsetIndex;
    protected FrontendQueryHandler queryHandler;
    private long lastInsertId;
    private MySqlDataSource dataSource;
    // 前端session
    private FrontendSession session;
    private volatile int txIsolation;
    private volatile boolean autoCommit = true;

    private static final long AUTH_TIMEOUT = 15 * 1000L;

    /**
     * 初始化DB的同时,绑定后端连接
     */
    public void initDB(BinaryPacket bin) {
        MySQLMessage mm = new MySQLMessage(bin.data);
        mm.position(1);
        String db = mm.readString();
        logger.debug("init db ==== " + db);

        // 检查schema是否已经设置
        if (schema != null) {
            if (schema.equals(db)) {
                logger.debug("init success");
                writeOk();
            } else {
                // TODO
            }
            return;
        }
        if (db == null) {
            // TODO
            return;
        } else {
            logger.debug("init success");
            this.schema = db;
            writeOk();
            return;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getCharsetIndex() {
        return charsetIndex;
    }

    public void setCharsetIndex(int charsetIndex) {
        this.charsetIndex = charsetIndex;
    }

    public FrontendQueryHandler getQueryHandler() {
        return queryHandler;
    }

    public void setQueryHandler(FrontendQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    public MySqlDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(MySqlDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void writeErrMessage(byte id, int errNo, String msg) {
        ErrorPacket err = new ErrorPacket();
        err.packetId = id;
        err.errno = errNo;
        err.message = encodeString(msg, charset);
        err.write(ctx);
    }

    private static byte[] encodeString(String src, String charset) {
        if (src == null) {
            return null;
        }
        if (charset == null) {
            return src.getBytes();
        }
        try {
            return src.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return src.getBytes();
        }
    }

    public void query(BinaryPacket bin) {
        if (queryHandler != null) {
            MySQLMessage mm = new MySQLMessage(bin.data);
            mm.position(1);
            String sql = null;
            try {
                sql = mm.readString(charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
            if (sql == null || sql.length() == 0) {
                logger.info("empty SQL");
                return;
            }
            queryHandler.query(sql);
        } else {
            logger.error("query error");
        }
    }

    /**
     * 获取已经状态同步过的backend
     */
    public BackendConnection getStateSyncBackend() {
        return null;
    }

    public void writeOk() {
        ByteBuf byteBuf = ctx.alloc().buffer(OkPacket.OK.length).writeBytes(OkPacket.OK);
        ctx.writeAndFlush(byteBuf);
    }

    public void close() {
        logger.info("close frontend connection, host:{}, port:{}", host, port);
        // TODO
    }
}
