package lgh.test.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.HashedWheelTimer;
import lgh.test.handler.echo.ClientIdleStateHandler;
import lgh.test.handler.echo.ConnectorIdleStateTrigger;
import lgh.test.handler.echo.ConnectorWatchDog;
import lgh.test.handler.echo.EchoClientHandler;

public class EchoClient {
	public final HashedWheelTimer timer = new HashedWheelTimer();
	public Bootstrap bootstrap;
	public Channel channel;
	public String host = "127.0.0.1";
	public int port = 7878;

	/**
     * @param args
     * @throws InterruptedException 
     * @throws IOException 
     */
    public  void start() throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        	bootstrap= new Bootstrap();
        	bootstrap.group(group)
            .channel(NioSocketChannel.class);
            
        	final ConnectorWatchDog watchdog = new ConnectorWatchDog(EchoClient.this,true);
        	final ChannelHandler[] handlers=new ChannelHandler[] {
        			watchdog,
        			new ClientIdleStateHandler(0, 4, 0, TimeUnit.SECONDS),  
                    new ConnectorIdleStateTrigger(),
                    new StringDecoder(),  
                    new StringEncoder(),  
                    new EchoClientHandler() };
        	
            bootstrap.handler(new ChannelInitializer<Channel>() {  
                @Override  
                protected void initChannel(Channel ch) throws Exception {  
                    ch.pipeline().addLast(handlers);  
                }  
            });  
            // 连接服务端
            channel=bootstrap.connect(host, port).sync().channel();
    }

	public static void main(String[] args) throws InterruptedException, IOException {
		EchoClient client = new EchoClient();
		client.start();
	}
}
