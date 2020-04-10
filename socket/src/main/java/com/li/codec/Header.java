package com.li.codec;

import lombok.Data;

import java.util.Map;

/**
 * @Description 消息头
 * @Author li-yuanwen
 * @Date 2020/4/10 11:53
 */
@Data
public class Header {

    /**
     * 校验码
     */
    private int crcCode;

    /**
     * 消息长度(包括消息头和消息体)
     */
    private int length;

    /**
     * sessionId
     */
    private long sessionId;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 消息优先级
     */
    private byte priority;

    /**
     * 可选字段,用于扩展消息头
     */
    private Map<String, Object> attachment;

    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionId=" + sessionId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
