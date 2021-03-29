package com.li.consumer;

import com.li.client.CuratorFrameworkFactoryBean;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 21:44
 * @Description:
 **/
public class ZkServiceConsumerTest {

    @Test
    public void testZkServiceConsumerByDicorvery() throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .namespace(CuratorFrameworkFactoryBean.NAME_SPACE)
                .build();

        curatorFramework.start();

        ZkServiceConsumer consumer = new ZkServiceConsumer(curatorFramework);

        String serviceName = "test-lvs";
        long s1 = System.currentTimeMillis();
        System.out.println(consumer.getTotalCount(serviceName));
        long s2 = System.currentTimeMillis();
        System.out.println("耗时：" + (s2 - s1));
        System.out.println(consumer.getMinServiceInstanceByDiscorvery(serviceName).getId());
        long s3 = System.currentTimeMillis();
        System.out.println("耗时：" + (s3 - s2));
        System.out.println(consumer.getMinServiceInstanceByDiscorvery(serviceName).getId());
        System.out.println("耗时：" + (System.currentTimeMillis() - s3));
        consumer.shutdown();
    }

    @Test
    public void testZkServiceConsumerByInstance() throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .namespace(CuratorFrameworkFactoryBean.NAME_SPACE)
                .build();

        curatorFramework.start();

        ZkServiceConsumer consumer = new ZkServiceConsumer(curatorFramework);

        String serviceName = "test-lvs";
        long s1 = System.currentTimeMillis();
        System.out.println(consumer.getTotalCount(serviceName));
        long s2 = System.currentTimeMillis();
        System.out.println("耗时：" + (s2 - s1));
        System.out.println(consumer.getMinServiceInstanceByCache(serviceName).getId());
        long s3 = System.currentTimeMillis();
        System.out.println("耗时：" + (s3 - s2));
        System.out.println(consumer.getMinServiceInstanceByCache(serviceName).getId());
        System.out.println("耗时：" + (System.currentTimeMillis() - s3));
        consumer.shutdown();
    }

}