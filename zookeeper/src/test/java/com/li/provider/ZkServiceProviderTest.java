package com.li.provider;

import com.li.client.CuratorFrameworkFactoryBean;
import com.li.client.InstanceDetail;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 21:46
 * @Description:
 **/
public class ZkServiceProviderTest {

    @Test
    public void testZkServiceProvider_1() throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);

        CuratorFramework curatorFramework1 = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .namespace(CuratorFrameworkFactoryBean.NAME_SPACE)
                .build();

        curatorFramework1.start();

        ZkServiceProvider provider1 = new ZkServiceProvider(curatorFramework1, "test-lvs");

        provider1.registerService("127.0.0.1", 14111, new InstanceDetail("127.0.0.1:14111"));

        provider1.updateCount(5);

        System.out.println("zookeeper service 1 start success!");
        
        Thread.sleep(60000);

        provider1.shutdown();
    }

    @Test
    public void testZkServiceProvider_2() throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);
        CuratorFramework curatorFramework2 = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .namespace(CuratorFrameworkFactoryBean.NAME_SPACE)
                .build();

        curatorFramework2.start();

        ZkServiceProvider provider2 = new ZkServiceProvider(curatorFramework2, "test-lvs");

        provider2.registerService("127.0.0.1", 15111, new InstanceDetail("127.0.0.1:15111"));

        provider2.updateCount(13);

        System.out.println("zookeeper service 2 start success!");

        Thread.sleep(60000);

        provider2.shutdown();
    }

}