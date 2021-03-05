package com.li.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.li.codec.MessageType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/8/23 12:02
 */
public class MessageProtoFactory {

    private static final int CRC_CODE = 0xabef0101;

    private static byte[] encode(MessageProto.Message message) {
        return message.toByteArray();
    }

    private static MessageProto.Message decode(byte[] body) throws InvalidProtocolBufferException {
        return MessageProto.Message.parseFrom(body);
    }

    public static MessageProto.Message createLoginAuthReqMessage() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();

        MessageProto.Header.Builder headerBuilder = MessageProto.Header.newBuilder();
        headerBuilder.setCrcCode(CRC_CODE);
        headerBuilder.setType(MessageType.LOGIN_REQ.getValue());
        builder.setHeader(headerBuilder.build());

        return builder.build();
    }

    // 登录

    public static MessageProto.Message createLoginAuthRespMessage(String loginResult) {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();

        MessageProto.Header.Builder headerBuilder = MessageProto.Header.newBuilder();
        headerBuilder.setCrcCode(CRC_CODE);
        headerBuilder.setType(MessageType.LOGIN_RESP.getValue());

        builder.setHeader(headerBuilder.build());
        builder.setBody(ByteString.copyFrom(loginResult.getBytes()));
        return builder.build();
    }


    // 心跳检测

    public static MessageProto.Message createHeartBeatReqMessage() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();

        MessageProto.Header.Builder headerBuilder = MessageProto.Header.newBuilder();
        headerBuilder.setCrcCode(CRC_CODE);
        headerBuilder.setType(MessageType.HEART_BEAT_REQ.getValue());

        builder.setHeader(headerBuilder.build());

        return builder.build();
    }

    public static MessageProto.Message createHeartBeatResqMessage() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();

        MessageProto.Header.Builder headerBuilder = MessageProto.Header.newBuilder();
        headerBuilder.setCrcCode(CRC_CODE);
        headerBuilder.setType(MessageType.HEART_BEAT_RESP.getValue());

        builder.setHeader(headerBuilder.build());

        return builder.build();
    }

    public static void main(String[] args) {

    }

}
