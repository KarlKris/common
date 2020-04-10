package com.li.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description 消息解码类
 * @Author li-yuanwen
 * @Date 2020/4/10 11:57
 */
public final class NettyMessageDecoder extends MessageToMessageDecoder<NettyMessage> {

    private MarshallingDecoder decoder;

    public NettyMessageDecoder() throws IOException {
        decoder = new MarshallingDecoder();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, List<Object> list) throws Exception {

    }
}
