package com.li.client;

import com.li.codec.Header;
import com.li.codec.MessageType;
import com.li.codec.NettyMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
        ctx.writeAndFlush(NettyMessage.getLoginAuthReqMessage());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 接收服务端安全认证响应
        NettyMessage message = (NettyMessage) msg;
        // 仅处理安全认证响应
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.LOGIN_RESP.getValue()) {
            Byte body = (Byte) message.getBody();
            if (body != (byte) 0) {
                ctx.close();
            } else {
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
