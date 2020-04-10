package com.li.server;

import com.li.handler.NettyServerMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * @Description 基于Netty的服务端Socket 入口
 * @Author li-yuanwen
 * @Date 2020/4/11 3:58
 */
@Slf4j
public class Server {

    // 端口号
    public static final int PORT = 65098;

    // NIO 线程组
    private EventLoopGroup boss;
    private EventLoopGroup workers;

    // Channel
    private Channel channel;




    /**
     * 启动初始化
     */
    private void init(){
        // 配置服务端线程组
        boss = new NioEventLoopGroup();
        workers = new NioEventLoopGroup();
    }


    /**
     * 服务开启
     */
    public void start(){
        init();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, workers)
                .channel(NioServerSocketChannel.class)
                // ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数
                // ，函数listen(int socketfd,int backlog)用来初始化服务端可连接队列
                // ，服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                // ，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new NettyServerMessageHandler());
        // 绑定端口号
        ChannelFuture future = bootstrap.bind(PORT);

        // 绑定服务器Channel
        channel = future.channel();
    }

    /**
     * 服务关闭
     */
    public void stop(){
        if (channel != null){
            channel.closeFuture().awaitUninterruptibly();
        }
        // 释放线程池资源
        boss.shutdownGracefully();
        workers.shutdownGracefully();
    }

}
