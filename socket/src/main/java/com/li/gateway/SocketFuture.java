package com.li.gateway;

import com.li.codec.protocol.IMessage;

import java.util.concurrent.CompletableFuture;

/**
 * @author li-yuanwen
 * 异步消息载体
 */
public class SocketFuture {

    /** 消息序号 **/
    private long sn;
    /** 消息结果 **/
    private CompletableFuture<IMessage> future;

    public void success(IMessage message) {
        this.future.complete(message);
    }
}
