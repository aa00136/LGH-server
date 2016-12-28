package lgh.test.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lgh.test.constant.MessageType;
import lgh.test.handler.heartbeat.ClientHandler;
import lgh.test.listener.ClientChannelFutureListener;

public class HeartBeatClient {
	private NioEventLoopGroup group;
	private Bootstrap bootstrap;
	private Channel channel;
	private String host;
	private int port = 9000;

	public HeartBeatClient(String host, int port) {
		this.host = host;
		this.port = port;
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						// 这个地方的 必须和服务端对应上。否则无法正常解码和编码
						pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 0, 4));
						pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 0));
						// 客户端的逻辑
						pipeline.addLast("handler", new ClientHandler(HeartBeatClient.this));
					}
				});
	}

	public synchronized void connectToServer() {
		if (channel != null && channel.isActive()) {
			return;
		}
		ChannelFuture future = bootstrap.connect(host, port);
		future.addListener(new ClientChannelFutureListener(HeartBeatClient.this));
	}

	public void sendData(String content) {
		if (channel != null && channel.isActive()) {
			ByteBuf buf = channel.alloc().buffer(5 + content.getBytes().length);
			buf.writeInt(5 + content.getBytes().length);
			buf.writeByte(MessageType.CUSTOM_MSG);
			buf.writeBytes(content.getBytes());
			channel.writeAndFlush(buf);
		}
	}

	public void close(){
		channel.disconnect();
		channel.close();
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public NioEventLoopGroup getGroup() {
		return group;
	}

	public void setGroup(NioEventLoopGroup group) {
		this.group = group;
	}

	public static void main(String[] args) {
		HeartBeatClient client = new HeartBeatClient("localhost", 8000);
		client.connectToServer();
	}
}
