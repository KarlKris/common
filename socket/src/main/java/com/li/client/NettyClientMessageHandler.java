package com.li.client;

import com.li.codec.protocol.MessageDecoder;
import com.li.codec.protocol.MessageEncoder;
import com.li.proto.MessageProto;
import com.li.ssl.factory.SslContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/4/10 2:47
 */
public class NettyClientMessageHandler extends ChannelInitializer<SocketChannel> {

    /** ssl模式 **/
    private String sslMode;

    public NettyClientMessageHandler(String sslMode) {
        this.sslMode = sslMode;
    }

    public NettyClientMessageHandler() { }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        SSLEngine sslEngine = SslContextFactory.getSslEngine(sslMode);
        if (sslEngine != null) {
            sslEngine.setUseClientMode(true);
            pipeline.addFirst("SslHandler", new SslHandler(sslEngine));
        }

        pipeline.addLast("MessageEncoder", new MessageEncoder());
        pipeline.addLast("MessageDecoder", new MessageDecoder(1024 * 1024, 1, 4));

//        pipeline.addLast("ProtobufVarint32FrameDecoder", new ProtobufVarint32FrameDecoder());
//        pipeline.addLast("ProtobufDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
//        pipeline.addLast("ProtobufVarint32LengthFieldPrepender", new ProtobufVarint32LengthFieldPrepender());
//        pipeline.addLast("ProtobufEncoder", new ProtobufEncoder());

        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS));

        pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(30));

        pipeline.addLast("HeartBeatReqHandler", new HeartBeatReqHandler());

        pipeline.addLast("LoginAuthHandler", new LoginAuthReqHandler());

        pipeline.addLast("ClientHandler", new ClientHandler());
    }
}
