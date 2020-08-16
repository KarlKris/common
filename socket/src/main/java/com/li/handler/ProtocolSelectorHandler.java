package com.li.handler;

import com.li.codec.NettyMessageDecoder;
import com.li.codec.NettyMessageEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @Description 协议选择处理器
 * @Author li-yuanwen
 * @Date 2020/7/15 4:31
 */
@Slf4j
public class ProtocolSelectorHandler extends ByteToMessageDecoder {

    /**
     * 默认暗号长度为23
     */
    private static final int MAX_LENGTH = 23;
    /**
     * WebSocket握手的协议前缀
     */
    private static final String WEBSOCKET_PREFIX = "GET /";

    /**
     * WEBSOCKET 握手数据包头
     */
    public final static short HANDSHAKE_PREFIX = ('G' << 8) + 'E';

    private final static Charset charset = StandardCharsets.UTF_8;
    private final static String END_TAG = "\r\n";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        String protocol = getBufStart(in);
//        log.info("协议选择处理器收到消息头:{}", protocol);
//        if (protocol.startsWith(WEBSOCKET_PREFIX)) {
//            webSocketAdd(ctx);
//        } else {
//            customizeAdd(ctx);
//        }
        short value = getBufStartShort(in);
        in.resetReaderIndex();
        log.info("协议选择处理器收到消息头:{}", value);
        if (value == HANDSHAKE_PREFIX) {
//            doHandshake(ctx.channel(), in);
            webSocketAdd(ctx);
        } else {
            customizeAdd(ctx);
        }
        ctx.pipeline().remove(this.getClass());
    }

    private String getBufStart(ByteBuf in) {
        int length = in.readableBytes();
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }

        // 标记读位置
        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        return new String(content, StandardCharsets.UTF_8);
    }

    private short getBufStartShort(ByteBuf in) {
        // 标记读位置
        in.markReaderIndex();
        return in.readShort();
    }

    private void webSocketAdd(ChannelHandlerContext ctx) {

        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        ctx.pipeline().addBefore("idleStateHandler", "http-codec", new HttpServerCodec());

        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        ctx.pipeline().addBefore("idleStateHandler", "aggregator", new HttpObjectAggregator(65535));

        //  主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
        ctx.pipeline().addBefore("idleStateHandler", "http-chunked", new ChunkedWriteHandler());

        ctx.pipeline().addBefore("idleStateHandler", "WebSocketAggregator", new WebSocketFrameAggregator(65535));

        // WebSocket数据压缩
        ctx.pipeline().addAfter("idleStateHandler", "webSocketServerCompressionHandler", new WebSocketServerCompressionHandler());

        //参数指的是contex_path 协议包长度限制
        ctx.pipeline().addAfter("idleStateHandler", "webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/"));

        // 自定义的数据帧处理
        ctx.pipeline().addAfter("idleStateHandler", "textWebSocketFrameHandler", new TextWebSocketFrameHandler());


    }

    private void customizeAdd(ChannelHandlerContext ctx) throws IOException {
        ctx.pipeline().addBefore("idleStateHandler", "MessageEncoder", new NettyMessageEncoder());
        ctx.pipeline().addBefore("idleStateHandler", "MessageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
        ctx.pipeline().addAfter("idleStateHandler", "LoginAuthRespHandler", new LoginAuthRespHandler());
        ctx.pipeline().addAfter("idleStateHandler", "HeartBeatHandler", new HeartBeatRespHandler());
        ctx.pipeline().addAfter("idleStateHandler", "readTimeoutHandler", new ReadTimeoutHandler(30));
    }

}
