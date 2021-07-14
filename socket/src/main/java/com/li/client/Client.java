package com.li.client;

import com.li.ssl.SSLMODE;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description 客户端
 * @Author li-yuanwen
 * @Date 2020/4/10 2:25
 */
@Slf4j
public class Client {

    /** ip地址 **/
    private String host;
    /** 端口地址 **/
    private int port;

    private EventLoopGroup group;
    private Channel channel;

    Client(String host, int port, EventLoopGroup group) {
        this.host = host;
        this.port = port;
        this.group = group;
    }

    Client(InetSocketAddress address, EventLoopGroup group) {
        this.host = address.getHostName();
        this.port = address.getPort();
        this.group = group;
    }


    public Client connect() {
        // 配置客户端
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1500)
                .handler(new NettyClientMessageHandler(SSLMODE.CSA.name()));

        ChannelFuture future = null;
        try {
            future = b.connect(host, port).sync();
            channel = future.channel();
            log.info("----------客户端连接[{}]成功---------", host + ":" + port);
        } catch (InterruptedException e) {
            log.error("客户端连接[{}:{}]失败", host, port);
        }
        return this;

    }

    public void close() {
        group.shutdownGracefully();
        channel.close();
    }



    public static void main(String[] args) throws InterruptedException {
        new Client("127.0.0.1", 11028, new NioEventLoopGroup()).connect();
    }

}
