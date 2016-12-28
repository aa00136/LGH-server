package lgh.test.handler.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lgh.test.constant.MessageType;

public abstract class CustomHeartbeatHandler extends SimpleChannelInboundHandler<ByteBuf> {
    protected String name;

    public CustomHeartbeatHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, ByteBuf byteBuf) throws Exception {
        if (byteBuf.getByte(4) == MessageType.PING_MSG) {
            sendPongMsg(context);
        } else if (byteBuf.getByte(4) == MessageType.PONG_MSG){
            System.out.println(name + " get pong msg from " + context.channel().remoteAddress());
        } else {
            handleData(context, byteBuf);
        }
    }

    protected void sendPingMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(MessageType.PING_MSG);
        buf.retain();
        context.writeAndFlush(buf);
        System.out.println(name + " sent ping msg to " + context.channel().remoteAddress());
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(MessageType.PONG_MSG);
        context.channel().writeAndFlush(buf);
        System.out.println(name + " sent pong msg to " + context.channel().remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is active---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is inactive---");
    }
    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf);
    
    protected abstract void handleReaderIdle(ChannelHandlerContext ctx) ;

    protected abstract void handleWriterIdle(ChannelHandlerContext ctx) ;

    protected abstract void handleAllIdle(ChannelHandlerContext ctx) ;
}
