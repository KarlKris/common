package com.li.handler;

import com.li.proto.MessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

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

        ctx.pipeline().addAfter("idleStateHandler", "MessageToMessageDecoder", new MessageToMessageDecoder<WebSocketFrame>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
                ByteBuf buf = msg.content();
                out.add(buf);
                buf.retain();
            }
        });

        ctx.pipeline().addAfter("idleStateHandler", "MessageToMessageEncoder", new MessageToMessageEncoder<MessageProto.MessageOrBuilder>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, MessageProto.MessageOrBuilder msg, List<Object> out) throws Exception {
                ByteBuf buf = null;
                if (msg instanceof MessageProto.Message) {
                    buf = wrappedBuffer(((MessageProto.Message) msg).toByteArray());
                }
                if (msg instanceof MessageProto.Message.Builder) {
                    buf = wrappedBuffer(((MessageProto.Message.Builder) msg).build().toByteArray());
                }

                // ==== 上面代码片段是拷贝自TCP ProtobufEncoder 源码 ====
                // 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的

                WebSocketFrame frame = new BinaryWebSocketFrame(buf);
                out.add(frame);
            }
        });

        // 协议包解码时指定Protobuf字节数实例化为MessageProto.Message类型
        ctx.pipeline().addLast("ProtobufDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));

        // 自定义的数据帧处理
//        ctx.pipeline().addAfter("idleStateHandler", "textWebSocketFrameHandler", new TextWebSocketFrameHandler());
        ctx.pipeline().addLast("WebSocketMessageHandler", new WebSocketMessageHandler());

    }

    private void customizeAdd(ChannelHandlerContext ctx) throws IOException {
//        ctx.pipeline().addBefore("idleStateHandler", "MessageEncoder", new NettyMessageEncoder());
//        ctx.pipeline().addBefore("idleStateHandler", "MessageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));

        ctx.pipeline().addBefore("idleStateHandler", "ProtobufVarint32FrameDecoder", new ProtobufVarint32FrameDecoder());
        ctx.pipeline().addBefore("idleStateHandler", "ProtobufDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
        ctx.pipeline().addBefore("idleStateHandler", "ProtobufVarint32LengthFieldPrepender", new ProtobufVarint32LengthFieldPrepender());
        ctx.pipeline().addBefore("idleStateHandler", "ProtobufEncoder", new ProtobufEncoder());

        ctx.pipeline().addAfter("idleStateHandler", "LoginAuthRespHandler", new LoginAuthRespHandler());
        ctx.pipeline().addAfter("idleStateHandler", "HeartBeatRespHandler", new HeartBeatRespHandler());
        ctx.pipeline().addAfter("idleStateHandler", "readTimeoutHandler", new ReadTimeoutHandler(30));
    }

}
