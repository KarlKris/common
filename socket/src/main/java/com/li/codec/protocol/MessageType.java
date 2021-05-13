package com.li.codec.protocol;

/**
 * @Description 消息头的type
 * @Author li-yuanwen
 * @Date 2020/4/11 1:15
 */
public interface MessageType {

    /**
     * 安全认证请求消息
     */
    byte LOGIN_REQ = 1;

    /**
     * 安全请求响应消息
     */
    byte LOGIN_RESP = 2;

    /**
     * 心跳检测请求消息
     */
    byte HEART_BEAT_REQ = 3;

    /**
     * 心跳检测响应消息
     */
    byte HEART_BEAT_RESP = 4;

    /**
     * 业务请求消息
     **/
    byte REQUEST = 5;

    /**
     * 业务响应消息
     **/
    byte RESPONSE = 6;

    /**
     * 请求转发
     */
    byte FORWARD_REQ = 7;

    /**
     * 响应转发
     */
    byte FORWARD_RESP = 8;


    static boolean isGateMessageType(byte type) {
        return type != REQUEST && type != RESPONSE;
    }

}
