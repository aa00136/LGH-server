package lgh.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lgh.test.handler.command.CommandDecoder;
import lgh.test.handler.command.CommandEncoder;
import lgh.test.handler.command.ServerCommandHandler;
import lgh.test.handler.command.ServerHeartBeatHandler;
import lgh.test.handler.command.ServerIdleStateTrigger;

public class CommandServer {
	private int port = 9000;

	public CommandServer(int port) {
		super();
		this.port = port;
	}

	public synchronized void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).localAddress(port)
				.childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("IdleStateHandler",new IdleStateHandler(0, 0, 5));
						pipeline.addLast("CommandEncoder", new CommandEncoder());
						pipeline.addLast("CommandDecoder", new CommandDecoder());
						pipeline.addLast("ServerIdleStateTrigger",new ServerIdleStateTrigger());
						pipeline.addLast("ServerHeartBeatHandler",new ServerHeartBeatHandler());
				        pipeline.addLast("ServerCommandHandler", new ServerCommandHandler());
					}
				});
		try {
			ChannelFuture future=bootstrap.bind().sync();
			System.out.println(CommandServer.class.getName() + " started and listen on " + future.channel().localAddress());
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		CommandServer server=new CommandServer(8000);
		server.start();
	}
}
