package com.li.codec.protocol.impl;

import lombok.Builder;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 网关服务器与外部通信消息头
 */
@Builder
@Getter
public class GateMessageHeader {

    /** 消息类型 **/
    private byte type;
    /** 消息总长度 **/
    private int length;
    /** 消息来源模块号 **/
    private int module;
    /** 消息来源命令号 **/
    private int command;
    /** 消息体是否压缩 **/
    private boolean zip;

    @Override
    public String toString() {
        return "GateMessageHeader{" +
                "type=" + type +
                ", length=" + length +
                ", module=" + module +
                ", command=" + command +
                ", zip=" + zip +
                '}';
    }
}
