package destiny.fate.common.net.handler.backend;

import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.handler.backend.pool.MySqlDataPool;
import destiny.fate.common.net.handler.frontend.FrontendConnection;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangtianlong01 on 2017/10/9.
 */
public class BackendConnection {

    private static final Logger logger = LoggerFactory.getLogger(BackendConnection.class);

    public int charsetIndex;

    public String charset;

    private long id;

    private ChannelHandlerContext ctx;

    /**
     * 当前连接所属的连接池
     */
    private MySqlDataPool mySqlDataPool;

    /**
     * 后端连接同步latch
     */
    public CountDownLatch syncLatch;

    public FrontendConnection frontend;

    /**
     * 前后端连接堆积的command,通过队列来实现线程间的无锁化
     */
    private ConcurrentLinkedQueue<Command> cmdQueue;

    public BackendConnection(MySqlDataPool mySqlDataPool) {
        this.mySqlDataPool = mySqlDataPool;
        this.syncLatch = new CountDownLatch(1);
        this.cmdQueue = new ConcurrentLinkedQueue<Command>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public boolean isAlive() {
        return ctx.channel().isActive();
    }

    public void close() {
        ctx.close();
    }

    public void discard() {
        mySqlDataPool.discard(this);
        close();
    }

    public void postCommand(Command command) {
        cmdQueue.offer(command);
    }

    public Command peekCommand() {
        return cmdQueue.peek();
    }

    public void fireCmd() {
        Command command = peekCommand();
        if (command != null) {
            ctx.writeAndFlush(command.getCmdByteBuf(ctx));
        }
    }

    public Command pollCommand() {
        return cmdQueue.poll();
    }

    public void setFrontend(FrontendConnection frontend) {
        this.frontend = frontend;
    }
}
