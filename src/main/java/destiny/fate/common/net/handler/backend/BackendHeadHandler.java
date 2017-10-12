package destiny.fate.common.net.handler.backend;

import io.netty.channel.ChannelHandlerAdapter;

/**
 * Created by zhangtianlong01 on 2017/10/12.
 */
public class BackendHeadHandler extends ChannelHandlerAdapter {

    public static final String HANDLER_NAME = "BackendHeadHandler";

    private BackendConnection source;

    public BackendHeadHandler(BackendConnection source) {
        this.source = source;
    }

    public BackendConnection getSource() {
        return source;
    }

    public void setSource(BackendConnection source) {
        this.source = source;
    }
}
