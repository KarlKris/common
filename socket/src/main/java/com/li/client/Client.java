package com.li.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 客户端
 * @Author li-yuanwen
 * @Date 2020/4/10 2:25
 */
@Slf4j
public class Client {

    public static final int PORT = 65098;
    public static final String HOST = "192.168.11.74";

    private EventLoopGroup group;
    private Channel channel;


    public void connect() {
        // 配置客户端
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientMessageHandler());

        ChannelFuture future = b.connect(HOST, PORT);
        channel = future.channel();
        log.debug("----------客户端连接-[{}]", HOST + ":" +PORT);
    }

    public void close(){
        channel.closeFuture();
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        new Client().connect();
    }

}
