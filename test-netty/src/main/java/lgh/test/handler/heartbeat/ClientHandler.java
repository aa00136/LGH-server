package lgh.test.handler.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lgh.test.client.HeartBeatClient;

public class ClientHandler extends CustomHeartbeatHandler {
    private HeartBeatClient client;
	
    public ClientHandler(HeartBeatClient client) {
        super("client");
        this.client=client;
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        System.out.println(name + " get content: " + content);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        client.connectToServer();
    }
    
    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        sendPingMsg(ctx);
    }
    
	@Override
	protected void handleReaderIdle(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleWriterIdle(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}
}
