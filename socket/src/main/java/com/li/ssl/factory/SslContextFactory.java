package com.li.ssl.factory;

import com.li.ssl.SSLMODE;
import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @Description 描述
 * @Author li-yuanwen
 * @Date 2020/9/14 4:10
 */
public class SslContextFactory {

    private static final String PROTOCOL = "TLS";
    private static SSLContext SERVER_CONTEXT;
    private static SSLContext CLIENT_CONTEXT;
    private static final String PASSWORD = "sslNetty";

    public static SSLEngine getSslEngine(String mode) {
        if (StringUtils.isEmpty(mode)) {
            return null;
        }

        if (!SSLMODE.contain(mode)) {
            return null;
        }

        SSLEngine sslEngine = null;
        switch (SSLMODE.valueOf(mode)) {
            case CA:
                sslEngine = getClientContext(
                        mode,
                        null,
                        System.getProperty("user.dir")
                                + "/common/socket/src/main/java/com/li/ssl/conf/oneway/sslSecureClient.jks")
                        .createSSLEngine();
                break;
            case CSA:
                sslEngine = getClientContext(
                        mode,
                        System.getProperty("user.dir")
                                + "/common/socket/src/main/java/com/li/ssl/conf/twoway/sslSecureServer.jks",
                        System.getProperty("user.dir")
                                + "/common/socket/src/main/java/com/li/ssl/conf/twoway/sslSecureClient.jks")
                        .createSSLEngine();
                sslEngine.setNeedClientAuth(true);
                break;
            default:
                throw new IllegalArgumentException("mode not set");
        }
        return sslEngine;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    public static SSLContext getServerContext(String tlsMode, String pkPath,
                                              String caPath) {
        if (SERVER_CONTEXT == null) {
            InputStream in = null;
            InputStream tIN = null;
            try {
                // Set up key manager factory to use our key store
                KeyManagerFactory kmf = null;
                if (pkPath != null) {
                    KeyStore ks = KeyStore.getInstance("JKS");
                    in = new FileInputStream(pkPath);
                    ks.load(in, PASSWORD.toCharArray());
                    kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(ks, PASSWORD.toCharArray());
                }
                TrustManagerFactory tf = null;
                if (caPath != null) {
                    KeyStore tks = KeyStore.getInstance("JKS");
                    tIN = new FileInputStream(caPath);
                    tks.load(tIN, PASSWORD.toCharArray());
                    // tks.load(tIN, "123456".toCharArray());
                    tf = TrustManagerFactory.getInstance("SunX509");
                    tf.init(tks);
                }
                // Initialize the SSLContext to work with our key managers.
                SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
                if (SSLMODE.CA.toString().equals(tlsMode))
                    SERVER_CONTEXT.init(kmf.getKeyManagers(), null, null);
                else if (SSLMODE.CSA.toString().equals(tlsMode)) {
                    SERVER_CONTEXT.init(kmf.getKeyManagers(),
                            tf.getTrustManagers(), null);
                } else {
                    throw new Error(
                            "Failed to initialize the server-side SSLContext"
                                    + tlsMode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error(
                        "Failed to initialize the server-side SSLContext", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                in = null;
                if (tIN != null)
                    try {
                        tIN.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                tIN = null;
            }
        }
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    public static SSLContext getClientContext(String tlsMode, String pkPath,
                                              String caPath) {
        if (CLIENT_CONTEXT == null) {
            InputStream in = null;
            InputStream tIN = null;
            try {
                // Set up key manager factory to use our key store
                KeyManagerFactory kmf = null;
                if (pkPath != null) {
                    KeyStore ks = KeyStore.getInstance("JKS");
                    in = new FileInputStream(pkPath);
                    ks.load(in, PASSWORD.toCharArray());
                    kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(ks, PASSWORD.toCharArray());
                }

                // Set up trust manager factory to use our key store
                // TrustManagerFactory tmf = TrustManagerFactory
                // .getInstance("SunX509");
                // tmf.init(ks);
                TrustManagerFactory tf = null;
                if (caPath != null) {
                    KeyStore tks = KeyStore.getInstance("JKS");
                    tIN = new FileInputStream(caPath);
                    tks.load(tIN, PASSWORD.toCharArray());
                    tf = TrustManagerFactory.getInstance("SunX509");
                    tf.init(tks);
                }
                // Initialize the SSLContext to work with our key managers.
                CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
                if (SSLMODE.CA.toString().equals(tlsMode))
                    CLIENT_CONTEXT.init(null,
                            tf == null ? null : tf.getTrustManagers(), null);
                else if (SSLMODE.CSA.toString().equals(tlsMode)) {
                    CLIENT_CONTEXT.init(kmf.getKeyManagers(),
                            tf.getTrustManagers(), null);
                } else {
                    throw new Error(
                            "Failed to initialize the client-side SSLContext"
                                    + tlsMode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error(
                        "Failed to initialize the client-side SSLContext", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                in = null;
            }
        }
        return CLIENT_CONTEXT;
    }
}
