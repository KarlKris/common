package com.li.client;

import com.li.codec.protocol.MessageCodecFactory;
import com.li.codec.protocol.MessageType;
import com.li.codec.protocol.impl.GateMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 握手和安全认证
 * @Author li-yuanwen
 * @Date 2020/4/11 1:12
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginAuthReqHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[客户端]三次握手成功后发送安全认证请求");
        // 三次握手成功后发送安全认证请求
        ctx.writeAndFlush(MessageCodecFactory.createLoginAuthReqMessage());

        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GateMessage) {
            GateMessage message = (GateMessage) msg;
            if (message.getMessageType() == MessageType.LOGIN_RESP) {
                if (!"SUCCESS".equals(new String(message.getBody()))) {
                    ctx.close();
                } else {
                    log.info("[客户端]收到验证成功消息");
                    ctx.fireChannelRead(msg);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("[客户端]登录抛出异常", cause);
    }
}
