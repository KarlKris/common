package com.li.client;

import com.google.protobuf.ByteString;
import com.li.codec.Header;
import com.li.codec.MessageType;
import com.li.codec.NettyMessage;
import com.li.proto.MessageProto;
import com.li.proto.MessageProtoFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
        ctx.writeAndFlush(MessageProtoFactory.createLoginAuthReqMessage());

        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 接收服务端安全认证响应
//        NettyMessage message = (NettyMessage) msg;
        MessageProto.Message message = (MessageProto.Message) msg;
        // 仅处理安全认证响应
        MessageProto.Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.LOGIN_RESP.getValue()) {
            ByteString body = message.getBody();
            String s = body.toStringUtf8();
            if (!"SUCCESS".equals(s)) {
                ctx.close();
            } else {
                log.info("[客户端]收到验证成功消息");
                ctx.fireChannelRead(msg);
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
