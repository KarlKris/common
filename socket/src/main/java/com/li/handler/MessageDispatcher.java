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
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.bind(ctx, localAddress, promise);
        log.info("MessageDispatcher---bind()");
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        log.info("MessageDispatcher---connect()");
    }


    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
        log.info("MessageDispatcher---read()");

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        log.info("MessageDispatcher---write()");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        log.info("MessageDispatcher---channelRead()");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("MessageDispatcher---channelActive()");
    }
}
