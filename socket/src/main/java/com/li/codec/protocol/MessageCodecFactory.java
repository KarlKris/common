package com.li.codec.protocol;

import com.li.codec.protocol.impl.GateMessage;
import com.li.codec.protocol.impl.GateMessageHeader;
import com.li.codec.protocol.impl.InnerMessage;
import com.li.codec.protocol.impl.InnerMessageHeader;
import com.li.session.Session;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author li-yuanwen
 */
public class MessageCodecFactory {

    public static GateMessage decodeGateMessage(ByteBuf in, byte messageType) {
        GateMessageHeader.GateMessageHeaderBuilder headerBuilder = GateMessageHeader.builder();
        GateMessageHeader header = headerBuilder.type(messageType)
                .length(in.readInt())
                .module(in.readInt())
                .command(in.readInt())
                .zip(in.readBoolean())
                .build();

        byte[] body = new byte[in.readInt()];
        in.readBytes(body);
        return new GateMessage(header, body);

    }

    public static void encodeGateMessage(GateMessage message, ByteBuf out) {
        out.writeByte(message.getMessageType());
        out.writeInt(message.getHeader().getLength());
        out.writeInt(message.getHeader().getModule());
        out.writeInt(message.getHeader().getCommand());
        out.writeBoolean(message.getHeader().isZip());

        int bodyLength = message.getBody().length;
        out.writeInt(bodyLength);
        out.writeBytes(message.getBody());

        // ?原因 这里为什么要减去5
        // 解：因为使用了Netty的LengthFieldBasedFrameDecoder来对消息解码，
        // 它是利用自定义消息中，带有数据包有效长度来解决TCP粘包现象
        // 这里的数据包中，先写入校验码（int,4个字节）,长度(int,4个字节),(有效内容)
        // 即需要满足0 = 数据包总长度 - lengthFieldOffset(有效长度下标) - lengthFieldLength(有效长度所占字节数) - 长度域的值(12)
        // 在心跳检测中  0 = 26 - 4 - 4 - 18，所以这里长度要减去消息类型字节数1和长度字节数4
        // https://www.jianshu.com/p/64dc7ee8c713
        int i = out.readableBytes();
        // ByteBuf index为1个字节,这里长度字段是从第4字节开始的
        out.setInt(1, i - 5);

    }

    public static InnerMessage decodeInnerMessage(ByteBuf in, byte messageType) {
        InnerMessageHeader.InnerMessageHeaderBuilder builder = InnerMessageHeader.builder();
        builder.type(messageType)
                .length(in.readInt())
                .sn(in.readLong())
                .module(in.readInt())
                .command(in.readInt())
                .zip(in.readBoolean())
                .sessionId(in.readLong());
        byte[] ip = new byte[in.readInt()];
        in.readBytes(ip);
        InnerMessageHeader header = builder.ip(new String(ip, StandardCharsets.UTF_8)).build();

        byte[] body = new byte[in.readInt()];
        in.readBytes(body);
        return new InnerMessage(header, body);
    }

    public static void encodeInnerMessage(InnerMessage message, ByteBuf out) {
        out.writeByte(message.getMessageType());
        out.writeInt(message.getHeader().getLength());
        out.writeLong(message.getHeader().getSn());
        out.writeInt(message.getHeader().getModule());
        out.writeInt(message.getHeader().getCommand());
        out.writeBoolean(message.getHeader().isZip());
        out.writeLong(message.getHeader().getSessionId());

        byte[] ipBytes = message.getHeader().getIp().getBytes(StandardCharsets.UTF_8);
        out.writeInt(ipBytes.length);
        out.writeBytes(ipBytes);

        int bodyLength = message.getBody().length;
        out.writeInt(bodyLength);
        out.writeBytes(message.getBody());

        // ?原因 这里为什么要减去8
        // 解：因为使用了Netty的LengthFieldBasedFrameDecoder来对消息解码，
        // 它是利用自定义消息中，带有数据包有效长度来解决TCP粘包现象
        // 这里的数据包中，先写入校验码（int,4个字节）,长度(int,4个字节),(有效内容)
        // 即需要满足0 = 数据包总长度 - lengthFieldOffset(有效长度下标) - lengthFieldLength(有效长度所占字节数) - 长度域的值(12)
        // 在心跳检测中  0 = 26 - 4 - 4 - 18，所以这里长度要减去消息类型字节数4和长度字节数4
        // https://www.jianshu.com/p/64dc7ee8c713
        int i = out.readableBytes();
        // ByteBuf index为1个字节,这里长度字段是从第4字节开始的
        out.setInt(1, i - 5);
    }

    public static GateMessage createLoginAuthReqMessage() {
        GateMessageHeader.GateMessageHeaderBuilder headerBuilder = GateMessageHeader.builder();
        GateMessageHeader header = headerBuilder
                .type(MessageType.LOGIN_REQ)
                .length(0)
                .module(0)
                .command(0)
                .zip(false)
                .build();

        return new GateMessage(header, new byte[0]);
    }

    public static GateMessage createLoginAuthRespMessage(GateMessage message, String body) {
        GateMessageHeader.GateMessageHeaderBuilder headerBuilder = GateMessageHeader.builder();
        GateMessageHeader header = headerBuilder.type(MessageType.LOGIN_RESP)
                .length(message.getHeader().getLength())
                .module(message.getHeader().getModule())
                .command(message.getHeader().getCommand())
                .zip(message.getHeader().isZip())
                .build();

        return new GateMessage(header, body.getBytes());
    }

    public static GateMessage createHeartBeatResqMessage(GateMessage message) {
        GateMessageHeader.GateMessageHeaderBuilder headerBuilder = GateMessageHeader.builder();
        GateMessageHeader header = headerBuilder.type(MessageType.HEART_BEAT_RESP)
                .length(message.getHeader().getLength())
                .module(message.getHeader().getModule())
                .command(message.getHeader().getCommand())
                .zip(message.getHeader().isZip())
                .build();

        return new GateMessage(header, new byte[0]);
    }

    public static GateMessage createHeartBeatReqMessage() {
        GateMessageHeader.GateMessageHeaderBuilder headerBuilder = GateMessageHeader.builder();
        GateMessageHeader header = headerBuilder
                .type(MessageType.HEART_BEAT_REQ)
                .length(0)
                .module(0)
                .command(0)
                .zip(false)
                .build();

        return new GateMessage(header, new byte[0]);
    }

    public static InnerMessage buildInnerMessageByGateMessageAndSession(GateMessage gateMessage, Session session, long sn) {
        InnerMessageHeader.InnerMessageHeaderBuilder builder = InnerMessageHeader.builder();
        InnerMessageHeader header = builder.type(MessageType.FORWARD_REQ)
                .ip(session.getIp())
                .sessionId(session.getIdentity())
                .zip(gateMessage.getHeader().isZip())
                .module(gateMessage.getHeader().getModule())
                .command(gateMessage.getHeader().getCommand())
                .sn(sn).length(0).build();

        return new InnerMessage(header, gateMessage.getBody());
    }
}
