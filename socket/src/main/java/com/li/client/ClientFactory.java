package com.li.client;

/**
 * @Description Netty Client Factory
 * @Author li-yuanwen
 * @Date 2021/4/13 10:43
 */
public class ClientFactory {


    public static Client newInstance(String host, int port) {
        return new Client(host, port);
    }

}
