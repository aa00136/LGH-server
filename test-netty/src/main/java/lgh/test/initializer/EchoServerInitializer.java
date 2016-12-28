package lgh.test.initializer;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lgh.test.handler.echo.AcceptorIdleStateTrigger;
import lgh.test.handler.echo.EchoServerHandler;

public class EchoServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 5, 0,TimeUnit.SECONDS));
        pipeline.addLast("AcceptorIdleStateTrigger", new AcceptorIdleStateTrigger());
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", new EchoServerHandler());
    }
}
