package com.li.handler;

import com.li.codec.Header;
import com.li.codec.MessageType;
import com.li.codec.NettyMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
        NettyMessage message = (NettyMessage) msg;
        Header header = message.getHeader();

        if (header != null && header.getType() == MessageType.HEART_BEAT_REQ.getValue()) {
            log.info("[服务端]收到客户端心跳检测-{}", message);

            NettyMessage nettyMessage = NettyMessage.getHeartBeatResqMessage();
            ctx.writeAndFlush(nettyMessage);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("[服务端]心跳响应异常", cause);
        ctx.fireExceptionCaught(cause);
    }
}
