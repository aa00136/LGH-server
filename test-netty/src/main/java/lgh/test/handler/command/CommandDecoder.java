package lgh.test.handler.command;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lgh.test.constant.CommandCode;
import lgh.test.dto.Command;
import lgh.test.dto.PingAckCommand;
import lgh.test.dto.PingCommand;

public class CommandDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int requestId=in.readInt();
		short commandCode=in.readShort();
		byte responseCode=in.readByte();
		int bodyLength=in.readInt();
		byte extentionLength=in.readByte();
        String body = readCommandContent(in, bodyLength);
        String extention = readCommandContent(in, extentionLength);
        
        Command cmd=null;
        switch (commandCode) {
		case CommandCode.PING_REQ:
			cmd=new PingCommand();
			break;
		case CommandCode.PING_RSP:
			cmd=new PingAckCommand();
			break;
		default:
			cmd=new Command();
			break;
		}
        cmd.setRequestId(requestId);
        cmd.setCommandCode(commandCode);
        cmd.setResponseCode(responseCode);
        cmd.setBody(body);
        cmd.setExtention(extention);
        
        out.add(cmd);
	}
	private String readCommandContent(ByteBuf buffer, int len) throws UnsupportedEncodingException {
        byte[] body = new byte[len];
        buffer.readBytes(body);
        return new String(body, "utf-8");
    }
}
