package com.li.client;

import com.li.codec.MessageType;
import com.li.codec.NettyMessage;
import com.li.proto.MessageProto;
import com.li.proto.MessageProtoFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
//        NettyMessage message = (NettyMessage) msg;
        MessageProto.Message message = (MessageProto.Message) msg;
        MessageProto.Header header = message.getHeader();

        if (header != null && header.getType() == MessageType.HEART_BEAT_RESP.getValue()) {
            // 心跳响应

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
                MessageProto.Message message = MessageProtoFactory.createHeartBeatReqMessage();
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
