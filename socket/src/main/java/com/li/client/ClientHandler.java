package com.li.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/4/10 2:50
 */
@Slf4j
public class ClientHandler extends ChannelDuplexHandler {

    public static final String FIRST_MESSAGE = "Hello, Server";

    private final ByteBuf buf;

    public ClientHandler() {
        byte[] bytes = FIRST_MESSAGE.getBytes();
        buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf tempBuf = (ByteBuf) msg;
        byte[] req = new byte[tempBuf.readableBytes()];
        tempBuf.readBytes(req);

        String s = new String(req, "UTF-8");
        log.debug(s);
    }
}
