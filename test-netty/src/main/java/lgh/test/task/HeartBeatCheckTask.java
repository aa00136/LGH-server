package lgh.test.task;

import io.netty.channel.ChannelHandlerContext;
import lgh.test.dto.PingCommand;

public class HeartBeatCheckTask implements Runnable {
	private ChannelHandlerContext ctx;
	
	public HeartBeatCheckTask(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
	}

	public void run() {
		PingCommand cmd=new PingCommand();
		System.out.println("ping:"+ctx.channel().remoteAddress());
		ctx.writeAndFlush(cmd);
	}

}
