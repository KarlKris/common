package com.li.codec.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author li-yuanwen
 */
@Slf4j
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf == null) {
            return null;
        }

        // 消息类型
        byte messageType = buf.readByte();
        if (MessageType.isGateMessageType(messageType)) {
            return MessageCodecFactory.decodeGateMessage(buf, messageType);
        }else {
            return MessageCodecFactory.decodeInnerMessage(buf, messageType);
        }

    }
}
