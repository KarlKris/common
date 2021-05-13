package com.li.codec.protocol;

/**
 * @author li-yuanwen
 * 通信消息载体
 */
public interface IMessage {

    /**
     * 返回通信消息类型
     * @return
     */
    byte getMessageType();


}
