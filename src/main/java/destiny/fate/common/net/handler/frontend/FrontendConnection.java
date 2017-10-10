package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.MySQLMessage;
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

    public void close() {
        logger.info("close frontend connection, host:{}, port:{}", host, port);
        // TODO
    }
}
