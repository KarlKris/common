package com.li.codec;

import com.li.codec.serialize.JSONSerializer;
import com.li.codec.serialize.Serializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/7/15 2:10
 */
public class JsonCodec {

    public static final JsonCodec INSTANCE = new JsonCodec();

    private final Map<Byte, Serializer> serializerMap;

    private JsonCodec() {
        serializerMap = new HashMap<Byte, Serializer>();

        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
    }


    public void encode(ByteBuf byteBuf, NettyMessage message) {

    }

    public NettyMessage decode(ByteBuf byteBuf) {
        return null;
    }
}
