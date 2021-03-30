package com.li.consumer;

import com.li.client.CuratorFrameworkFactoryBean;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 21:44
 * @Description:
 **/
public class ZkServiceConsumerTest {

    @Test
    public void testZkServiceConsumerByDiscorvery() throws Exception {
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
        System.out.println(consumer.getMinServiceInstance(serviceName).getId());
        long s3 = System.currentTimeMillis();
        System.out.println("耗时：" + (s3 - s2));
        System.out.println(consumer.getMinServiceInstance(serviceName).getId());
        System.out.println("耗时：" + (System.currentTimeMillis() - s3));
        consumer.shutdown();
    }


    @Test
    public void testZNodeStat() throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .namespace(CuratorFrameworkFactoryBean.NAME_SPACE)
                .build();

        curatorFramework.start();

        Stat stat = new Stat();
        curatorFramework.getData().storingStatIn(stat).forPath("/test-lvs_discorvery/test-lvs_count");
        System.out.println(stat.getVersion());
        System.out.println(stat.getCversion());
    }

    @Test
    public void testZkServiceConsumer() throws InterruptedException {
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

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 300; i++) {
            try{
                long s2 = System.currentTimeMillis();
                String str = consumer.getMinServiceInstance(serviceName).getId();
                long s3 = System.currentTimeMillis();
                System.out.println(str + " 耗时：" + (s3 - s2));
                Thread.sleep(100);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        consumer.shutdown();
        curatorFramework.close();
    }
}