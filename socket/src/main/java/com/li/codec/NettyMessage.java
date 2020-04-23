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

    // 登录

    public static NettyMessage getLoginAuthReqMessage(){
        NettyMessage message = new NettyMessage();
        message.setHeader(Header.getLoginAuthHeader());
        return message;
    }

    public static NettyMessage getLoginAuthRespMessage(byte loginResult) {
        NettyMessage message = new NettyMessage();
        message.setHeader(Header.getLoginAuthRespHeader());
        message.setBody(loginResult);
        return message;
    }


    // 心跳检测

    public static NettyMessage getHeartBeatReqMessage() {
        NettyMessage message = new NettyMessage();
        message.setHeader(Header.getHeartBeatReqHeader());
        return message;
    }

    public static NettyMessage getHeartBeatResqMessage() {
        NettyMessage message = new NettyMessage();
        message.setHeader(Header.getHeartBeatRespHeader());
        return message;
    }
}
