package com.li.codec.protocol.impl;

import com.li.codec.protocol.IMessage;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 网关服务器与外部通信消息
 */
@Getter
public class GateMessage implements IMessage {

    /** 消息头 **/
    private GateMessageHeader header;
    /** 消息体 **/
    private byte[] body;

    @Override
    public byte getMessageType() {
        return header.getType();
    }

    public GateMessage(GateMessageHeader header, byte[] body) {
        this.header = header;
        this.body = body;
    }
}
