package lgh.test.listener;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lgh.test.client.HeartBeatClient;

public class ClientChannelFutureListener implements ChannelFutureListener {
	private HeartBeatClient client;
	private static int reConnectCount=0;

	public ClientChannelFutureListener(HeartBeatClient client) {
		this.client=client;
	}

	public void operationComplete(ChannelFuture future) throws Exception {
		if (future.isSuccess()) {
			reConnectCount=0;
			client.setChannel(future.channel());
			client.sendData("hello server!");
			System.out.println("Connect to server successfully!");
		} else {
			reConnectCount++;
			if(reConnectCount>10){
				System.out.println("try reconnect 10 times faild!");
				client.getGroup().shutdownGracefully();
				return ;
			}
			
			System.out.println("Failed to connect to server, reconnect after 5s");
			future.channel().eventLoop().schedule(new Runnable() {
				public void run() {
					client.connectToServer();
				}
			}, 5, TimeUnit.SECONDS);
		}
	}

}
