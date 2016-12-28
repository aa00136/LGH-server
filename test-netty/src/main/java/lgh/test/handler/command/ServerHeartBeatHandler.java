package lgh.test.handler.command;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lgh.test.constant.CommandCode;
import lgh.test.dto.Command;
import lgh.test.dto.PingCommandResponse;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof Command){
			Command cmd=(Command)msg;
			if(cmd.getCommandCode()==CommandCode.PING_REQ){
				PingCommandResponse cmdRes=new PingCommandResponse(cmd.getRequestId());
				ctx.writeAndFlush(cmdRes);
			}
		}
        ctx.fireChannelRead(msg);
    }
}
