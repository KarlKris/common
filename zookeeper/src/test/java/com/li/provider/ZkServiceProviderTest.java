package com.li.provider;

import com.li.client.CuratorFrameworkFactoryBean;
import com.li.client.InstanceDetail;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import java.io.IOException;
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

    @Test
    public void testZkServiceProvider_3() {
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
                ZkServiceProvider provider = new ZkServiceProvider(curatorFramework, "test-lvs");
                provider.registerService("127.0.0.1", i, new InstanceDetail("127.0.0.1:" + i));

                provider.updateCount(new Random().nextInt(100));

                System.out.println("zookeeper service " + i + " start success!");

                executorService.schedule(() -> {
                    try {
                        provider.shutdown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 600, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.schedule(curatorFramework::close, 600, TimeUnit.SECONDS);
        System.out.println("300台服务启动成功");
        executorService.shutdown();
    }

}