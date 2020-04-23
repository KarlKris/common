package com.li.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 消息解码类
 * @Author li-yuanwen
 * @Date 2020/4/10 11:57
 */
@Slf4j
public final class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private MarshallingDecoder decoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        decoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        log.info("decoder-[{}]", frame);

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionId(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int size = frame.readInt();
        if (size > 0) {
            Map<String, Object> attchment = new HashMap<String, Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attchment.put(key, decoder.decode(frame));
            }
            key = null;
            keyArray = null;
            header.setAttachment(attchment);
        }
        message.setHeader(header);

        int readableBytes = frame.readableBytes();
        if (readableBytes > 4) {
            message.setBody(decoder.decode(frame));
        }

        log.info("decode-{}", message);
        return message;
    }
}
