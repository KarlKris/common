package com.li.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/27 16:09
 * @Description: Zookeeper Curator FrameWork Client 工厂
 **/
@Slf4j
public class CuratorFrameworkFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean {

    /**
     * 命名空间
     */
    public static final String NAME_SPACE = "service-zookeeper-namespace";


    private CuratorFramework client = null;

    /**
     * zookeeper 地址
     */
    @Value("${zookeeper.url:}")
    private String zookeeperUrl;


    /**
     * zookeeper 服务发现节点名称
     */
    @Value("${zookeeper.service.name:}")
    private String serviceName;

    /**
     * 进程服务端口
     */
    @Value("${server.port:}")
    private int port;

    @Override
    public CuratorFramework getObject()  {
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return CuratorFramework.class;
    }

    @Override
    public void afterPropertiesSet()  {
        if (!StringUtils.isEmpty(zookeeperUrl)) {
            // 两次重连的等待的时间为60s 重连次数上限是3次
            ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);
            this.client = CuratorFrameworkFactory
                    .builder()
                    .connectString(zookeeperUrl)
                    .retryPolicy(retry)
                    .namespace(NAME_SPACE)
                    .build();

            this.client.start();
        }

        log.warn("进程启动不连接zookeeper");
    }
}
