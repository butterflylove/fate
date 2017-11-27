package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.handler.factory.FrontConnectionFactory;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 前端连接收集器
 * @author zhangtianlong
 */
public class FrontendCollectHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FrontConnectionFactory.class);

    public static ConcurrentHashMap<Long, FrontendConnection> frontends = new ConcurrentHashMap<Long, FrontendConnection>();

    protected FrontendConnection source;

    public FrontendCollectHandler(FrontendConnection source) {
        this.source = source;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("a connection accept!");
        logger.info("frontend connection id={}", source.getId());
        frontends.put(source.getId(), source);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("a connection break!");
//        frontends.remove(source.getId());
//        source.close();
        super.channelInactive(ctx);
    }
}
