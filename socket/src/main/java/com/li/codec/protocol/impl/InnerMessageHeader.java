package com.li.codec.protocol.impl;

import lombok.Builder;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 网关服务器与内部服务器通信消息头
 */
@Getter
@Builder
public class InnerMessageHeader {

    /** 消息类型 **/
    private byte type;
    /** 消息总长度 **/
    private int length;
    /** 消息序号 **/
    private long sn;
    /** 消息来源模块号 **/
    private int module;
    /** 消息来源命令号 **/
    private int command;
    /** 消息体是否压缩 **/
    private boolean zip;
    /** 外部的标识id **/
    private long sessionId;
    /** 外部通信的ip地址 **/
    private String ip;

}
