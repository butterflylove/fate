package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.pool.MySqlDataSource;
import destiny.fate.common.net.handler.node.ResponseHandler;
import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.ErrorPacket;
import destiny.fate.common.net.protocol.MySQLMessage;
import destiny.fate.common.net.protocol.OkPacket;
import destiny.fate.common.net.protocol.util.ErrorCode;
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

    public FrontendConnection() {
        this.session = new FrontendSession(this);
    }

    /**
     * 初始化DB的同时,绑定后端连接
     */
    public void initDB(BinaryPacket bin) {
        MySQLMessage mm = new MySQLMessage(bin.data);
        mm.position(1);
        String db = mm.readString();
        logger.info("init db ==== " + db);

        // 检查schema是否已经设置
        if (schema != null) {
            if (schema.equals(db)) {
                logger.info("init success");
                writeOk();
            } else {
                writeErrMessage(ErrorCode.ER_DBACCESS_DENIED_ERROR, "Not allowed to change the database!");
            }
            return;
        }
        if (db == null) {
            writeErrMessage(ErrorCode.ER_BAD_DB_ERROR, "Unknown database");
        } else {
            logger.info("init success");
            this.schema = db;
            writeOk();
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

    public long getLastInsertId() {
        return lastInsertId;
    }

    public void setLastInsertId(long lastInsertId) {
        this.lastInsertId = lastInsertId;
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

    public void writeErrMessage(int errNo, String msg) {
        logger.warn(String.format("[FrontendConnection]ErrorNo=%d,ErrorMsg=%s", errNo, msg));
        writeErrMessage((byte) 1, errNo, msg);
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
                writeErrMessage(ErrorCode.ER_UNKNOWN_CHARACTER_SET, "Unknown charset '" + charset + "'");
                return;
            }
            if (sql == null || sql.length() == 0) {
                logger.info("empty SQL");
                writeErrMessage(ErrorCode.ER_NOT_ALLOWED_COMMAND, "Empty SQL!");
                return;
            }
            // 执行SQL
            queryHandler.query(sql);
        } else {
            logger.error("query error");
            writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Query Unsupported!");
        }
    }

    /**
     * 调用后端数据库去执行
     */
    public void execute(String sql, int type) {
        this.schema="test";
        if (schema == null) {
            writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "No database selected");
        } else {
            session.execute(sql, type);
        }
    }

    /**
     * 获取已经状态同步过的backend
     */
    public BackendConnection getStateSyncBackend() {
        BackendConnection backend = dataSource.getBackend();
        backend.setFrontend(this);
        return backend;
    }

    public void writeOk() {
        ByteBuf byteBuf = ctx.alloc().buffer(OkPacket.OK.length).writeBytes(OkPacket.OK);
        ctx.writeAndFlush(byteBuf);
    }

    public void close() {
        logger.info("close frontend connection, host:{}, port:{}", host, port);
        // TODO
    }

    public ResponseHandler getResponseHandler() {
        return session.getResponseHandler();
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }
}
