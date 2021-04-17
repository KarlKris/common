package com.li.provider;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description ZkServiceProvider 工厂
 * @Author li-yuanwen
 * @Date 2021/4/12 15:31
 */
public class ZkServiceProviderFactoryBean implements FactoryBean<ZkServiceProvider>, InitializingBean {

    /**
     * zookeeper 服务发现节点名称
     */
    @Value("${zookeeper.service.name:}")
    private String serviceName;



    @Autowired
    private CuratorFramework curatorFramework;

    private ZkServiceProvider provider;

    @Override
    public ZkServiceProvider getObject() throws Exception {
        return provider;
    }

    @Override
    public Class<?> getObjectType() {
        return ZkServiceProvider.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.provider = new ZkServiceProvider(curatorFramework, serviceName);
    }
}
