package com.li.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
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

        pipeline.addLast(new FixedLengthFrameDecoder(1024));
        pipeline.addLast(new StringDecoder());

        pipeline.addLast(new MessageDispatcher());

//        pipeline.addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.get))
    }
}
