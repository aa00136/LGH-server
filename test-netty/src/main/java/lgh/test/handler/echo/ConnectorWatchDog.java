package lgh.test.handler.echo;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lgh.test.client.EchoClient;

@Sharable
public class ConnectorWatchDog extends ChannelInboundHandlerAdapter implements TimerTask {
    private volatile boolean reconnect = true;  
    private int attempts;
    private EchoClient client;
    
	public ConnectorWatchDog(EchoClient client,boolean reconnect) {
		super();
		this.reconnect = reconnect;
		this.client=client;
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		attempts=0;
		System.out.println("connect success!");
        ctx.fireChannelActive();
    }
	@Override  
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {  
		System.out.println("链接关闭");
		if(reconnect){
			if(attempts<=10){
				attempts++;
				client.timer.newTimeout(this, 2, TimeUnit.SECONDS);
			}
		}
		ctx.fireChannelInactive();
	}
	public void run(Timeout timeout) throws Exception {
		final ChannelFuture future;
		final ChannelHandler[] handlers=new ChannelHandler[] {
    			ConnectorWatchDog.this,
    			new ClientIdleStateHandler(0, 4, 0, TimeUnit.SECONDS),  
                new ConnectorIdleStateTrigger(),
                new StringDecoder(),  
                new StringEncoder(),  
                new EchoClientHandler() };
		client.bootstrap.handler(new ChannelInitializer<Channel>() {  
            @Override  
            protected void initChannel(Channel channel) throws Exception {  
            	channel.pipeline().addLast(handlers);  
            }  
        });
		
		future=client.bootstrap.connect(client.host,client.port);
		future.addListener(new ChannelFutureListener() {  
            public void operationComplete(ChannelFuture f) throws Exception {  
                if (!f.isSuccess()) {  
                    System.out.println("重连失败");  
                    f.channel().pipeline().fireChannelInactive();  
                }else{  
                    System.out.println("重连成功");  
                    client.channel=future.channel();
                }  
            }  
        });  
	}
}
