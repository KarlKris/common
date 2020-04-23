package com.li.handler;

import com.li.codec.NettyMessageDecoder;
import com.li.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description Netty消息处理器
 * @Author li-yuanwen
 * @Date 2020/4/11 5:10
 */
@Slf4j
public class NettyServerMessageHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("MessageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
        pipeline.addLast("MessageEncoder", new NettyMessageEncoder());
        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(30));
        pipeline.addLast("LoginAuthHandler", new LoginAuthRespHandler());
        pipeline.addLast("HeartBeatHandler", new HeartBeatRespHandler());

//        pipeline.addLast(new MessageDispatcher());

    }
}
