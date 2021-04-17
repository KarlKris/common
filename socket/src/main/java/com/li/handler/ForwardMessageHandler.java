package com.li.handler;

import com.li.codec.MessageType;
import com.li.proto.MessageProto;
import com.li.session.ForwardDispatcher;
import com.li.session.Session;
import com.li.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 消息转发处理器
 * @Author li-yuanwen
 * @Date 2021/4/13 10:15
 */
@Slf4j
public class ForwardMessageHandler extends ChannelDuplexHandler {


    /**
     * 消息转发分发器
     **/
    private final ForwardDispatcher dispatcher;
    /**
     * Session管理器
     **/
    private final SessionManager sessionManager;

    public ForwardMessageHandler(ForwardDispatcher dispatcher, SessionManager sessionManager) {
        this.dispatcher = dispatcher;
        this.sessionManager = sessionManager;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Session session = sessionManager.createSession(channel);
        if (log.isInfoEnabled()) {
            log.info("[{}]连接创建转发会话[{}]", session.getId(), channel);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Session session = sessionManager.deleteSession(channel);
        if (log.isInfoEnabled()) {
            log.info("[{}]连接断开转发会话[{}]", session.getId(), channel);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = false;
        try {
            if (msg instanceof MessageProto.Message) {

                release = true;

                MessageProto.Message message = (MessageProto.Message) msg;
                int messageType = message.getHeader().getType();
                if (messageType == MessageType.FORWARD_REQ.getValue()) {
                    Channel channel = ctx.channel();
                    Session session = sessionManager.getSession(channel);
                    if (!session.hasIdentity()) {
                        return;
                    }

                    dispatcher.forward(message, session, sessionManager.getServerId());
                }
            }
        } finally {
            // 释放bytebuf
            if (release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
