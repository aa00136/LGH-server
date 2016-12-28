package lgh.test.handler.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleState state=((IdleStateEvent)evt).state();
			if(state==IdleState.READER_IDLE){
				throw new Exception("IdleState exception!");
			}
		}
    }
}
