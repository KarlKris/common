package com.li.codec;

/**
 * @Description 消息头的type
 * @Author li-yuanwen
 * @Date 2020/4/11 1:15
 */
public enum MessageType {

    /**
     * 安全认证请求消息
     */
    LOGIN_REQ((byte) 1),

    /**
     * 安全请求响应消息
     */
    LOGIN_RESP((byte) 2),

    /**
     * 心跳检测请求消息
     */
    HEART_BEAT_REQ((byte) 3),

    /**
     * 心跳检测响应消息
     */
    HEART_BEAT_RESP((byte) 4),

    /**
     * 业务请求消息
     **/
    REQUEST((byte) 5),

    /**
     * 业务响应消息
     **/
    RESPONSE((byte) 6);

    private byte value;

    public byte getValue() {
        return value;
    }

    MessageType(byte value) {
        this.value = value;
    }
}
