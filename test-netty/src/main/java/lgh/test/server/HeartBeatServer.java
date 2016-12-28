package lgh.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lgh.test.handler.heartbeat.ServerHandler;

public class HeartBeatServer {
	private int port = 9000;

	public HeartBeatServer(int port) {
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
						pipeline.addLast("decoder",new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 0));
				        pipeline.addLast("handler", new ServerHandler());
					}
				});
		try {
			ChannelFuture future=bootstrap.bind().sync();
			System.out.println(HeartBeatServer.class.getName() + " started and listen on " + future.channel().localAddress());
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			try {
				bossGroup.shutdownGracefully().sync();
				workerGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args){
		HeartBeatServer server=new HeartBeatServer(8000);
		server.start();
	}
}
