package com.li.server;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/4/11 20:45
 * @Description:
 **/
@Component
public class ServerFactoryBean implements FactoryBean<Server>, InitializingBean, BeanPostProcessor {

    // 端口号
    @Value("${netty.server.port}")
    private int port;

    private Server server;

    @Override
    public Server getObject() throws Exception {
        return server;
    }

    @Override
    public Class<?> getObjectType() {
        return Server.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.server = new Server(port);
    }
}
