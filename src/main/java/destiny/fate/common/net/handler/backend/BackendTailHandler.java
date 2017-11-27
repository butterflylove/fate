package destiny.fate.common.net.handler.backend;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangtianlong01 on 2017/11/27.
 */
public class BackendTailHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackendTailHandler.class);

    private BackendConnection source;

    public BackendTailHandler(BackendConnection source) {
        this.source = source;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Backend exception caught", cause);
        source.discard();
    }
}
