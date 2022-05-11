package per.hrj.mq.conumser;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import per.hrj.mq.msg.BindingQueueMessage;

import java.util.logging.Logger;

public class Consumer {

    private Logger logger = Logger.getLogger("Consumer");

    public static final String address = "127.0.0.1";

    public static final int serverPort = 8080;

    private Channel channel = null;

    public void start(String queueName) {
        ChannelFuture channelFuture = null;
        try {
            channelFuture = new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            channel = ch;
                            ch.pipeline()
                                    .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new SimpleChannelInboundHandler<Object>() {

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            logger.info("consumer receive message : " + msg);
                                        }
                                    });
                        }
                    }).connect(address, serverPort)
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("consumer connect server successfully");
        BindingQueueMessage bindingQueueMessage = new BindingQueueMessage();
        bindingQueueMessage.setQueueName(queueName);
        channel.writeAndFlush(bindingQueueMessage);
        logger.info("send binding message");

    }

}
