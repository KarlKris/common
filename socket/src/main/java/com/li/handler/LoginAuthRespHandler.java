package com.li.handler;

import com.li.codec.protocol.MessageCodecFactory;
import com.li.codec.protocol.MessageType;
import com.li.codec.protocol.impl.GateMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
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
    public static String LOGIN_SUCCESS = "SUCCESS";
    public static String LOGIN_FAIL = "FAIL";

    // 已登录的客户端地址,防止重复登录
    private Map<String, Boolean> loginIps = new ConcurrentHashMap<String, Boolean>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GateMessage) {
            GateMessage message = (GateMessage) msg;
            if (message.getMessageType() == MessageType.LOGIN_REQ) {
                String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();

                GateMessage respMessage;
                // 重复登录，拒绝
                if (loginIps.containsKey(ip)) {
                    respMessage = MessageCodecFactory.createLoginAuthRespMessage(message, LOGIN_FAIL);
                } else {
                    respMessage = MessageCodecFactory.createLoginAuthRespMessage(message, LOGIN_SUCCESS);
                    loginIps.put(ip, true);

                    log.info("[服务端]收到ip-[{}]的客户端登录请求，返回消息为-[{}]", ip, respMessage.getBody());
                }
                ctx.writeAndFlush(respMessage);

                // 释放请求消息
                ReferenceCountUtil.release(msg);
            }
        }else {
            ctx.fireChannelRead(msg);
        }

    }
}
