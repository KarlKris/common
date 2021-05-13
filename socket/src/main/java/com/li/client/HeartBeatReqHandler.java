package com.li.client;

import com.li.codec.protocol.MessageCodecFactory;
import com.li.codec.protocol.MessageType;
import com.li.codec.protocol.impl.GateMessage;
import com.li.proto.MessageProto;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 心跳检测
 * @Author li-yuanwen
 * @Date 2020/4/11 7:31
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartBeatReqHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GateMessage) {
            if (((GateMessage) msg).getMessageType() == MessageType.HEART_BEAT_RESP) {
                // 释放请求消息
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("[客户端]心跳检测");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                GateMessage message = MessageCodecFactory.createHeartBeatReqMessage();
                ctx.writeAndFlush(message);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("[客户端]心跳检测抛出异常", cause);
    }
}
