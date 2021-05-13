package com.li.codec.protocol;

import com.li.codec.protocol.impl.GateMessage;
import com.li.codec.protocol.impl.InnerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author li-yuanwen
 *
 */
public class MessageEncoder extends MessageToByteEncoder<IMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMessage msg, ByteBuf out) throws Exception {
        if (MessageType.isGateMessageType(msg.getMessageType())) {
            MessageCodecFactory.encodeGateMessage((GateMessage) msg, out);
        }else {
            MessageCodecFactory.encodeInnerMessage((InnerMessage) msg, out);
        }
    }
}
