package destiny.fate.common.net.handler.frontend;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class FrontendAuthenticator extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FrontendAuthenticator.class);

    public byte[] seed;

    protected FrontendConnection source;

    public FrontendAuthenticator(FrontendConnection source) {
        this.source = source;
    }

    /**
     * 发送HandShake包
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

}
