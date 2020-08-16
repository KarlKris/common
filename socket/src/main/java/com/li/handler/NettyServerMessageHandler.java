package com.li.handler;

import com.li.codec.NettyMessageDecoder;
import com.li.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description Netty消息处理器
 * @Author li-yuanwen
 * @Date 2020/4/11 5:10
 */
@Slf4j
public class NettyServerMessageHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

//        //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
//        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
//        //以块的方式来写的处理器
//        pipeline.addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
//        //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
//        pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(8192));
//
//        //ws://server:port/context_path
//        //ws://localhost:9999/ws
//        //参数指的是contex_path
//        pipeline.addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("li"));

        pipeline.addLast("protocolSelectorHandler", new ProtocolSelectorHandler());

        // 心跳检测
        pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));


//        pipeline.addLast(new MessageDispatcher());

    }
}
