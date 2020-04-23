package com.li.handler;

import com.li.codec.Header;
import com.li.codec.MessageType;
import com.li.codec.NettyMessage;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 握手和安全认证响应
 * @Author li-yuanwen
 * @Date 2020/4/11 1:29
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginAuthRespHandler extends ChannelDuplexHandler {
    // 响应消息体
    public static byte LOGIN_SUCCESS = 0;
    public static byte LOGIN_FAIL = -1;

    // 已登录的客户端地址,防止重复登录
    private Map<String, Boolean> loginIps = new ConcurrentHashMap<String, Boolean>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 仅处理登录请求
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.LOGIN_REQ.getValue()) {
            // 响应消息
            NettyMessage respMessage = null;

            String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
            // 重复登录，拒绝
            if (loginIps.containsKey(ip)) {
                respMessage = NettyMessage.getLoginAuthRespMessage(LOGIN_FAIL);
            }else {
                respMessage = NettyMessage.getLoginAuthRespMessage(LOGIN_SUCCESS);
                loginIps.put(ip, true);

                log.info("[服务端]收到ip-[{}]的客户端登录请求，返回消息为-[{}]", ip, respMessage.getBody());
            }
            ctx.writeAndFlush(respMessage);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("[服务端]登录响应异常", cause);
        ctx.fireExceptionCaught(cause);
    }
}
