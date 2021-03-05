package com.li.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/8/29 12:40
 */
@Slf4j
public class FatherHandler extends ChannelDuplexHandler {

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info("父类ChannelHandler-[FatherHandler]执行方法---------bind");
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.info("父类ChannelHandler-[FatherHandler]执行方法---------read");
        super.read(ctx);
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("父类ChannelHandler-[FatherHandler]执行方法---------channelRegistered");
        super.channelRegistered(ctx);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("父类ChannelHandler-[FatherHandler]执行方法---------channelActive");
        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("父类ChannelHandler-[FatherHandler]执行方法---------channelRead");
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("父类ChannelHandler-[FatherHandler]执行方法---------channelReadComplete");
        super.channelReadComplete(ctx);
    }
}
