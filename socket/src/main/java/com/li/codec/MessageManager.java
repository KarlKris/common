package com.li.codec;

import com.li.gateway.SocketFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 *
 * 消息管理
 */
public class MessageManager {

    /** 消息序号 **/
    private AtomicLong messageSnGenerator = new AtomicLong(0);
    /** 异步消息结果 **/
    private ConcurrentHashMap<Long, SocketFuture> futures = new ConcurrentHashMap<>(5000);


    public long getNextSn() {
        return messageSnGenerator.incrementAndGet();
    }

}
