package com.li.handler;

import com.li.codec.NettyMessageDecoder;
import com.li.codec.NettyMessageEncoder;
import com.li.ssl.factory.SslContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @Description Netty消息处理器
 * @Author li-yuanwen
 * @Date 2020/4/11 5:10
 */
@Slf4j
public class NettyServerMessageHandler extends ChannelInitializer<SocketChannel> {

    /** ssl模式 **/
    private String sslMode;

    public NettyServerMessageHandler(String sslMode) {
        this.sslMode = sslMode;
    }

    public NettyServerMessageHandler() { }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        SSLEngine sslEngine = SslContextFactory.getSslEngine(sslMode);
        if (sslEngine != null) {
            sslEngine.setUseClientMode(false);
            pipeline.addFirst("SslHandler", new SslHandler(sslEngine));
        }

        pipeline.addLast("protocolSelectorHandler", new ProtocolSelectorHandler());
        // 心跳检测
        pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));


        pipeline.addLast(new MessageDispatcher());

    }
}
