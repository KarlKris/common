package com.li.provider;

import com.li.client.CuratorFrameworkFactoryBean;
import com.li.node.InstanceDetail;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        provider1.registerService("127.0.0.1", 14111, new InstanceDetail("12_1", "127.0.0.1:14111"));

        provider1.updateCount(5);

        System.out.println("zookeeper service 1 start success!");

        Thread.sleep(30000);

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

        Stat stat = new Stat();
        curatorFramework2.getData().storingStatIn(stat).forPath("/test-lvs_discorvery/test-lvs_count");

        System.out.println(stat.getVersion());

        provider2.registerService("127.0.0.1", 15111, new InstanceDetail("12_2", "127.0.0.1:15111"));

        stat = new Stat();
        curatorFramework2.getData().storingStatIn(stat).forPath("/test-lvs_discorvery/test-lvs_count");

        System.out.println(stat.getVersion());

        provider2.updateCount(13);
        stat = new Stat();
        curatorFramework2.getData().storingStatIn(stat).forPath("/test-lvs_discorvery/test-lvs_count");

        System.out.println(stat.getVersion());

        System.out.println("zookeeper service 2 start success!");

        Thread.sleep(60000);

        provider2.shutdown();
    }

    @Test
    public void testZkServiceProvider_3() throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(5000, 3);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(4);
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .namespace(CuratorFrameworkFactoryBean.NAME_SPACE)
                .build();

        curatorFramework.start();
        for (int i = 0; i < 300; i++) {

            try {
                int j = i;
                ZkServiceProvider provider = new ZkServiceProvider(curatorFramework, "test-lvs");
                provider.registerService("127.0.0.1", i, new InstanceDetail("12_" + i, "127.0.0.1:" + i));

                provider.updateCount(new Random().nextInt(100) + 1);

                System.out.println("zookeeper service " + j + " start success!");

                int delaySecond = new Random().nextInt(15) + 60;
                executorService.schedule(() -> {
                    try {
                        provider.updateCount(new Random().nextInt(100) + 1);
                        System.out.println("zookeeper service " + j + " change count!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, delaySecond, TimeUnit.SECONDS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("300台服务启动成功");
        Thread.sleep(100000);
        executorService.shutdown();
        curatorFramework.close();
    }

}