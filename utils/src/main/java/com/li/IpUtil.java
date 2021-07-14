package com.li;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 00:12
 * @Description:
 **/
public class IpUtil {

    /**
     * 计算InetSocketAddress
     **/
    public static InetSocketAddress calInetSocketAddressByAddress(String addr) {
        int colonIndex = addr.lastIndexOf(":");
        if (colonIndex < 0) {
            return new InetSocketAddress(Integer.parseInt(addr));
        }
        int port = Integer.parseInt((addr.substring(colonIndex + 1)));
        if (colonIndex > 0) {
            String host = addr.substring(0, colonIndex);
            if (!"*".equals(host)) {
                return new InetSocketAddress(host, port);
            }
        }

        return new InetSocketAddress(port);
    }

    /**
     * 获取内网ip地址
     */
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }
}
