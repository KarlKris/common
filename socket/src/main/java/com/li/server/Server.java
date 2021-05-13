package com.li.server;

import com.li.handler.NettyServerMessageHandler;
import com.li.ssl.SSLMODE;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;


/**
 * @Description 基于Netty的服务端Socket 入口
 * @Author li-yuanwen
 * @Date 2020/4/11 3:58
 */
@Slf4j
public class Server implements DisposableBean {

    // 端口号
    private int port;

    // NIO 线程组
    private EventLoopGroup boss;
    private EventLoopGroup workers;

    // Channel
    private Channel channel;

    public Server(int port) {
        this.port = port;
    }


    /**
     * 启动初始化
     */
    private void init() {
        // 配置服务端线程组
        boss = new NioEventLoopGroup(1);
        workers = new NioEventLoopGroup();
    }


    /**
     * 服务开启
     */
    public void start() throws InterruptedException {
        init();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, workers)
                .channel(NioServerSocketChannel.class)
                // ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数
                // ，函数listen(int socketfd,int backlog)用来初始化服务端可连接队列
                // ，服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                // ，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 这个参数表示允许重复使用本地地址和端口。
                // 某个服务器进程占用了TCP的80端口进行监听，此时再次监听该端口就会返回错误，
                // 使用该参数就可以解决问题，该参数允许共用该端口，这个在服务器程序中比较常使用。
                .option(ChannelOption.SO_REUSEADDR, true)
                // 父类Handler的是加入到ChannelPipeline中
                // 与 ServerBootrapAcceptor一起为新连接做处理
                // 新连接完成后只有子Handler才会加到Channel中
//                .handler(new FatherHandler())
                .childHandler(new NettyServerMessageHandler());
        // 绑定端口号
        ChannelFuture future = bootstrap.bind(port).sync();

        // 绑定服务器Channel
        channel = future.channel();

        log.info("-------服务器启动成功[{}]---------", port);

        channel.closeFuture().addListener((ChannelFutureListener) future1 -> {
            log.warn("端口-[{}]链路-[{}]关闭", port, channel.toString());
            stop();
        });
    }


    /**
     * 服务关闭
     */
    public void stop() {
        log.warn("关闭stop");
        // 释放线程池资源
        boss.shutdownGracefully();
        workers.shutdownGracefully();
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    public static void main(String[] args) throws InterruptedException {
        new Server(11028).start();
    }
}
