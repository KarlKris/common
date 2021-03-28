package com.li.common;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/27 19:57
 * @Description: Byte相关工具类
 **/
public class ByteUtils {


    /**
     * int 转 byte[]   低字节在前（低字节序）
     */
    public static byte[] toByteArray(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }


    /**
     * byte[] 转 int 低字节在前（低字节序）
     */
    public static int toInt(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }


}
