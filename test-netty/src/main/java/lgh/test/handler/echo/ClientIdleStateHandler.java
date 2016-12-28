package lgh.test.handler.echo;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ClientIdleStateHandler extends IdleStateHandler{

	public ClientIdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
		super(readerIdleTime, writerIdleTime, allIdleTime, unit);
	}
	
}
