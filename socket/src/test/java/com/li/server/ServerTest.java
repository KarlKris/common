package com.li.server;

import com.li.client.Client;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

@Slf4j
public class ServerTest {

    @org.junit.Test
    public void start() throws InterruptedException{

        Client client = new Client();

        client.connect();


    }

}