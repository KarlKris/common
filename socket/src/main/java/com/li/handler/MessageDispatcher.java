package com.li.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @Description 消息分发处理器
 * @Author li-yuanwen
 * @Date 2020/4/6 23:31
 */
@ChannelHandler.Sharable
@Slf4j
public class MessageDispatcher extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("MessageDispatcher---channelRead()-[{}]", msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("MessageDispatcher---channelActive()");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出现未知异常,关闭channel", cause);
        ctx.channel().close();
    }
}
