package com.li.handler;

import com.li.proto.MessageProto;
import com.li.proto.MessageProtoFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/8/23 18:32
 */
@Slf4j
public class WebSocketMessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        log.info("收到消息：{}", msg.toString());

        MessageProto.Message resqMessage = MessageProtoFactory.createHeartBeatResqMessage();
        ctx.channel().writeAndFlush(resqMessage);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("web socket 握手成功。");
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String requestUri = handshakeComplete.requestUri();
            log.info("requestUri:[{}]", requestUri);
            String subproTocol = handshakeComplete.selectedSubprotocol();
            log.info("subproTocol:[{}]", subproTocol);
            handshakeComplete.requestHeaders().forEach(entry -> log.info("header key:[{}] value:[{}]", entry.getKey(), entry.getValue()));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常", cause);
    }
}
