package com.li.codec.serialize;

import com.alibaba.fastjson.JSON;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/7/15 1:56
 */
public class JSONSerializer implements Serializer {


    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
