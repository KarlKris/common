package com.li.client;

import com.li.IpUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * @description Netty Client Factory
 * @date 2021/4/13 10:43
 */
public class ClientFactory {

    private EventLoopGroup group = new NioEventLoopGroup();

    /** 持活客户端 **/
    private ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();

    public Client getClient(String address) {
        return clients.computeIfAbsent(address
                , k -> new Client(IpUtil.calInetSocketAddressByAddress(address), group).connect());
    }

}
