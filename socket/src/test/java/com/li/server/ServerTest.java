package com.li.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

@Slf4j
public class ServerTest {

    @org.junit.Test
    public void start() throws InterruptedException, IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress("192.168.11.83", 8888), 60);
        channel.configureBlocking(false);
        ServerSocket socket = channel.socket();
        Thread.sleep(10000);
        boolean run = true;
        int i = 0;
        System.out.println("服务端开始接受连接");
        while (run) {
            Socket accept = socket.accept();
            accept.close();
            System.out.println("服务端收到客户端连接:" + (++i));
        }
        socket.close();
        channel.close();
    }

}