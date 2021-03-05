package com.li.client;

import java.io.IOException;
import java.net.Socket;

public class ClientTest {

    public final int port = 8888;
    public final String HOST = "192.168.11.83";

    @org.junit.Test
    public void connect() throws IOException {
        for (int i = 0; i < 100; i++) {
            Socket socket = new Socket(HOST, port);
            socket.close();
            System.out.println("客户端连接个数:" + i);
        }
    }
}