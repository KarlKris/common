package com.li.client;

import com.li.proto.MessageProto;
import com.li.proto.MessageProtoFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/4/10 2:50
 */
@Slf4j
public class ClientHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().scheduleAtFixedRate(() -> {
            log.info("------定时任务-------------");
            ctx.writeAndFlush(MessageProtoFactory.createServiceReqMessage("service msg : " + new Random().nextInt(100)));
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProto.Message message = (MessageProto.Message) msg;
        log.info("收到业务消息[{}]", message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[客户端]发生异常", cause);
    }
}
