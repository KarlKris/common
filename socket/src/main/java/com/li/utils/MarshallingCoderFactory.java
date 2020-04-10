package com.li.utils;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * @Description Marshalling编解码工厂
 * @Author li-yuanwen
 * @Date 2020/4/10 12:15
 */
public class MarshallingCoderFactory {

    public static Marshaller buildMarshaller() throws IOException {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");

        final MarshallingConfiguration configuration = new MarshallingConfiguration();

        configuration.setVersion(8);

        return factory.createMarshaller(configuration);
    }

    public static Unmarshaller buildUnmarshaller() throws IOException {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");

        final MarshallingConfiguration configuration = new MarshallingConfiguration();

        configuration.setVersion(8);

        return factory.createUnmarshaller(configuration);
    }
}
