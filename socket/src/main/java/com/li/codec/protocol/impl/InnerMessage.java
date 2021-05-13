package com.li.codec.protocol.impl;

import com.li.codec.protocol.IMessage;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 网关服务器与内部服务器通信消息
 */
@Getter
public class InnerMessage implements IMessage {

    /** 消息头 **/
    private InnerMessageHeader header;
    /** 消息体 **/
    private byte[] body;

    @Override
    public byte getMessageType() {
        return header.getType();
    }

    public InnerMessage(InnerMessageHeader header, byte[] body) {
        this.header = header;
        this.body = body;
    }
}
