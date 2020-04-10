package com.li.codec;

import com.li.utils.MarshallingCoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description 消息编码类
 * @Author li-yuanwen
 * @Date 2020/4/10 11:58
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    private MarshallingEncoder encoder;

    public NettyMessageEncoder() throws IOException {
        encoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, List<Object> list) throws Exception {
        if (nettyMessage == null || nettyMessage.getHeader() == null) {
            throw new Exception("The encode message is null");
        }

        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(nettyMessage.getHeader().getCrcCode());
        sendBuf.writeInt(nettyMessage.getHeader().getLength());
        sendBuf.writeLong(nettyMessage.getHeader().getSessionId());
        sendBuf.writeByte(nettyMessage.getHeader().getType());
        sendBuf.writeByte(nettyMessage.getHeader().getPriority());
        sendBuf.writeInt(nettyMessage.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> entry : nettyMessage.getHeader().getAttachment().entrySet()) {
            key = entry.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);

            value = entry.getValue();
            encoder.encode(value, sendBuf);
        }

        Object body = nettyMessage.getBody();
        if (body != null){
            encoder.encode(body, sendBuf);
        }else {
            sendBuf.writeInt(0);
            sendBuf.setInt(4, sendBuf.readableBytes());
        }
    }
}
