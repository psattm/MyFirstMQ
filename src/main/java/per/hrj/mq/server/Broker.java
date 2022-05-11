package per.hrj.mq.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import per.hrj.mq.msg.BindingQueueMessage;
import per.hrj.mq.msg.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author hrj
 * 实现简单的队列管理的MQ
 * 实现思路：
 */
public class Broker {

    private Logger logger = Logger.getLogger("Broker");

    public static final int serverPort = 8080;

    private Map<String, BlockingQueue<Object>> queueMap = new ConcurrentHashMap<>();

    private Map<String, List<Channel>> channelMap = new ConcurrentHashMap<>();


    public void createQueue(String name) {
        synchronized (Broker.class) {
            BlockingQueue<Object> messageArrayBlockingQueue = new ArrayBlockingQueue<>(100);
            queueMap.put(name, messageArrayBlockingQueue);
            List<Channel> channels = new ArrayList<>();
            channelMap.put(name, channels);
        }
        resolveMessage(name);

    }

    public void resolveMessage(String name) {
        new Thread(() -> {
            while (true) {
                if(queueMap.get(name).size() != 0){
                    // 找到对应的channel进行转发
                    Object message = queueMap.get(name).poll();
                    for (Channel channel : channelMap.get(name)) {
                        channel.writeAndFlush(message);
                    }
                }
            }
        }).start();
    }

    public void startServer() {
        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup worker = new NioEventLoopGroup(4);
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            logger.info("connect..." + ch);
                            ch.pipeline()
                                    .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new SimpleChannelInboundHandler<BindingQueueMessage>() {

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, BindingQueueMessage msg) throws Exception {
                                            logger.info(ctx.channel() + "listen queue : " + msg.getQueueName());
                                            channelMap.get(msg.getQueueName()).add(ctx.channel());
                                        }
                                    })
                                    .addLast(new SimpleChannelInboundHandler<Message>() {

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
                                            logger.info("broker receive msg : " + msg);
                                            String targetName = msg.getTargetName();
                                            queueMap.get(targetName).offer(msg.getData());
                                        }

                                    });
                        }
                    })
                    .bind(serverPort)
                    .sync();
            logger.info("broker server start...");
            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("broker server stop...");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
