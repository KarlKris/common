package com.li.handler;

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

import java.io.IOException;

/**
 * @Description 心跳检测
 * @Author li-yuanwen
 * @Date 2020/4/11 8:11
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartBeatRespHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GateMessage) {
            GateMessage message = (GateMessage) msg;
            if (message.getMessageType() == MessageType.HEART_BEAT_REQ) {

                GateMessage resqMessage = MessageCodecFactory.createHeartBeatResqMessage(message);
                ctx.writeAndFlush(resqMessage);

                // 释放请求消息
                ReferenceCountUtil.release(msg);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("[服务端]心跳检测");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state()== IdleState.READER_IDLE){
                log.info("[服务端]指定时间内未收到消息,关闭channel");
                ctx.channel().close();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("[服务端]发生IOException,关闭客户端连接", cause);
            ctx.channel().close();
        }else {
            log.error("[服务端]心跳响应异常", cause);
        }
    }
}
