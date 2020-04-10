package com.li.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

import java.io.IOException;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/4/10 13:02
 */
public class ChannelBufferByteInput implements ByteInput {
    private ByteBuf buf;

    public ChannelBufferByteInput(ByteBuf buf) {
        this.buf = buf;
    }

    public int read() throws IOException {
        if (buf.isReadable()){
            return buf.readByte() & 0xff;
        }
        return -1;
    }

    public int read(byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    public int read(byte[] bytes, int i, int i1) throws IOException {
        int available = available();
        if (available == 0){
            return -1;
        }

        i1 = Math.min(available, i1);
        buf.readBytes(bytes, i, i1);
        return i1;
    }

    public int available() throws IOException {
        return buf.readableBytes();
    }

    public long skip(long l) throws IOException {
        int readable = buf.readableBytes();
        if (readable < l) {
            l = readable;
        }
        buf.readerIndex((int) (buf.readerIndex() + l));
        return l;
    }

    public void close() throws IOException {
        // nothing to do
    }
}
