package lgh.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lgh.test.initializer.EchoServerInitializer;

public class EchoServer {
    private static final int port = 7878;
    
    public static void main(String[] args){
        EchoServer server=new EchoServer();
        server.start();
    }
    public void start(){
    	EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.localAddress(port);
            bootstrap.childHandler(new EchoServerInitializer());
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}