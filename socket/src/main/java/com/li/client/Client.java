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

import java.util.concurrent.*;

/**
 * @Description 客户端
 * @Author li-yuanwen
 * @Date 2020/4/10 2:25
 */
@Slf4j
public class Client {

    public static final int PORT = 11028;
    public static final String HOST = "192.168.11.83";

    private EventLoopGroup group;
    private Channel channel;

    // 尝试重连次数
    public static final int MAX_CONNECT_NUM = 5;
    public int connectNum = 0;
    private ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L
            , TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("客户端断线重连单线程执行器"));


    public void connect() throws InterruptedException {
        try {
            // 配置客户端
            group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1500)
                    .handler(new NettyClientMessageHandler());

            ChannelFuture future = b.connect(HOST, PORT).sync();
            channel = future.channel();
            log.info("----------客户端连接-[{}]", HOST + ":" + PORT);

            channel.closeFuture().sync();
        } finally {
            // 先释放资源，在重连
            group.shutdownGracefully();
            executorService.execute(this::doExecuteReconnect);
        }
    }

    private void doExecuteReconnect() {
        if (connectNum++ >= MAX_CONNECT_NUM) {
            // 关闭线程池
            executorService.shutdown();
            return;
        }
        try {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws InterruptedException {
        new Client().connect();
    }

}
