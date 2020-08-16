package com.li.client;

import com.li.codec.NettyMessageDecoder;
import com.li.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/4/10 2:47
 */
public class NettyClientMessageHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("MessageEncoder", new NettyMessageEncoder());
        pipeline.addLast("MessageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
//
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));

        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(30));

        pipeline.addLast("LoginAuthHandler", new LoginAuthReqHandler());
        pipeline.addLast("HeartBeatHandler", new HeartBeatReqHandler());


//        pipeline.addLast(new ClientHandler());
    }
}
