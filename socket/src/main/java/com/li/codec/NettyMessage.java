package com.li.codec;

import lombok.Data;

/**
 * @Description 私有协议
 * @Author li-yuanwen
 * @Date 2020/4/10 11:51
 */
@Data
public class NettyMessage {

    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private Object body;


    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
