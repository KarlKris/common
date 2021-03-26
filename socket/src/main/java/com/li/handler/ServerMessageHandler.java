package com.li.handler;

import com.li.proto.MessageProto;
import com.li.session.MessageDispatcher;
import com.li.session.Session;
import com.li.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 业务处理逻辑入口
 * @Author li-yuanwen
 * @Date 2021/3/16 15:06
 */
@ChannelHandler.Sharable
@Slf4j
public class ServerMessageHandler extends ChannelDuplexHandler {

    /**
     * 消息分发器
     **/
    private final MessageDispatcher dispatcher;
    /**
     * Session管理器
     **/
    private final SessionManager sessionManager;

    public ServerMessageHandler(MessageDispatcher dispatcher, SessionManager sessionManager) {
        this.dispatcher = dispatcher;
        this.sessionManager = sessionManager;
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
        Channel channel = ctx.channel();
        Session session = sessionManager.getSession(channel);
        MessageProto.Message message = (MessageProto.Message) msg;
        dispatcher.dispatcher(message, session);
    }
}
