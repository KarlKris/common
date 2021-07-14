package com.li.handler;

import com.li.codec.protocol.MessageType;
import com.li.codec.protocol.impl.GateMessage;
import com.li.gateway.GatewayManager;
import com.li.session.Session;
import com.li.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author li-yuanwen
 *
 * 外部消息转发到内部处理器
 */
@ChannelHandler.Sharable
@Slf4j
public class GatewayHandler extends ChannelDuplexHandler {

    /** Session管理 **/
    private final SessionManager sessionManager;

    /** 网关管理 **/
    private GatewayManager gatewayManager;

    public GatewayHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.gatewayManager = new GatewayManager();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Session session = sessionManager.createSession(channel);
        if (log.isInfoEnabled()) {
            log.info("[{}]连接创建会话[{}]", session.getId(), channel);
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Session session = sessionManager.deleteSession(channel);
        if (log.isInfoEnabled()) {
            log.info("[{}]连接断开会话[{}]", session.getId(), channel);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GateMessage) {

            GateMessage message = (GateMessage) msg;
            if (message.getMessageType() != MessageType.REQUEST) {
                log.error("网关服务收到非请求消息[{}]", message.getMessageType());
                // 释放请求消息
                ReferenceCountUtil.release(msg);
                return;
            }

            Channel channel = ctx.channel();
            Session session = sessionManager.getSession(channel);
            // 消息处理
            gatewayManager.forward(message, session);

        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
