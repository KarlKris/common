package com.li.client;

import com.li.codec.Header;
import com.li.codec.MessageType;
import com.li.codec.NettyMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
        NettyMessage message = (NettyMessage) msg;
        Header header = message.getHeader();

        if (header != null && header.getType() == MessageType.LOGIN_RESP.getValue()) {
            // 成功登录后,每个0.5s向服务端发送心跳检测
            ctx.executor().scheduleAtFixedRate(() -> {
                NettyMessage m = NettyMessage.getHeartBeatReqMessage();
                ctx.writeAndFlush(m);
            }, 0, 1000, TimeUnit.MILLISECONDS);
        } else if (header != null && header.getType() == MessageType.HEART_BEAT_RESP.getValue()) {
            // 心跳响应
            log.info("[客户端]收到服务端的心跳响应消息-{}", message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("[客户端]心跳检测抛出异常", cause);
    }
}
