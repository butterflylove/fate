package destiny.fate.common.net.handler.frontend;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public abstract class AbstractFrontendConnection {

    protected static final int PACKET_HEADER_SIZE = 4;

    protected ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
