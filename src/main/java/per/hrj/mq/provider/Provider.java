package per.hrj.mq.provider;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import per.hrj.mq.msg.BindingQueueMessage;
import per.hrj.mq.msg.Message;

import java.util.logging.Logger;

public class Provider {


    private Logger logger = Logger.getLogger("Provider");

    public static final String address = "127.0.0.1";

    public static final int serverPort = 8080;

    private Channel channel = null;

    public void start() {
        ChannelFuture channelFuture = null;
        try {
            channelFuture = new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            channel = ch;
                        }
                    }).connect(address, serverPort)
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("provider connect server successfully");
        // channel = channelFuture.channel();


    }


    public void sendMessage(String queueName, Object data) {
        Message message = new Message();
        message.setTargetName(queueName);
        message.setData(data);
        channel.writeAndFlush(message);
        System.out.println("send : " + channel);
    }


}
