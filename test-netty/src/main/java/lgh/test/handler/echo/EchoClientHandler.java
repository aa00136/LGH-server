package lgh.test.handler.echo;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class EchoClientHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("激活时间是：" + new Date());
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("停止时间是：" + new Date());
	}

	@Override  
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
		String message=(String) msg;
		System.out.println(message);
		ReferenceCountUtil.release(msg);
	}
}
