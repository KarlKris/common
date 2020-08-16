package com.li.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * @Description 消息编码类
 * @Author li-yuanwen
 * @Date 2020/4/10 11:58
 */
@Slf4j
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    private MarshallingEncoder encoder;

    public NettyMessageEncoder() throws IOException {
        encoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, ByteBuf byteBuf) throws Exception {
        if (nettyMessage == null || nettyMessage.getHeader() == null) {
            throw new Exception("The encode message is null");
        }

        log.info("encode-{}", nettyMessage);

        byteBuf.writeInt(nettyMessage.getHeader().getCrcCode());
        byteBuf.writeInt(nettyMessage.getHeader().getLength());
        byteBuf.writeLong(nettyMessage.getHeader().getSessionId());
        byteBuf.writeByte(nettyMessage.getHeader().getType());
        byteBuf.writeByte(nettyMessage.getHeader().getPriority());
        byteBuf.writeInt(nettyMessage.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> entry : nettyMessage.getHeader().getAttachment().entrySet()) {
            key = entry.getKey();
            keyArray = key.getBytes("UTF-8");
            byteBuf.writeInt(keyArray.length);
            byteBuf.writeBytes(keyArray);

            value = entry.getValue();
            encoder.encode(value, byteBuf);
        }

        Object body = nettyMessage.getBody();
        if (body != null) {
            encoder.encode(body, byteBuf);
        } else {
            byteBuf.writeInt(0);
        }
        // ?原因 这里为什么要减去8
        // 解：因为使用了Netty的LengthFieldBasedFrameDecoder来对消息解码，
        // 它是利用自定义消息中，带有数据包有效长度来解决TCP粘包现象
        // 这里的数据包中，先写入校验码（int,4个字节）,长度(int,4个字节),(有效内容)
        // 即需要满足0 = 数据包总长度 - lengthFieldOffset(有效长度下标) - lengthFieldLength(有效长度所占字节数) - 长度域的值(12)
        // 在心跳检测中  0 = 26 - 4 - 4 - 18，所以这里长度要减去校验码字节数4和长度字节数4
        // https://www.jianshu.com/p/64dc7ee8c713
        int i = byteBuf.readableBytes();
        // ByteBuf index为1个字节,这里长度字段是从第4字节开始的
        byteBuf.setInt(4, i - 8);

        log.info("encoder-[{}]", byteBuf);
    }

}
