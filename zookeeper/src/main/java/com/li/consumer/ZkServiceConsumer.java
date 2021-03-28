package com.li.consumer;

import com.li.client.InstanceDetail;
import com.li.common.ByteUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.li.provider.ZkServiceProvider.COUNT;
import static com.li.provider.ZkServiceProvider.SLASH;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 15:48
 * @Description: zookeeper 服务使用者
 **/
public class ZkServiceConsumer {

    /**
     * zookeeper 客户端
     */
    private CuratorFramework curatorFramework;

    /**
     * 服务发现
     */
    private ConcurrentHashMap<String, ServiceDiscovery<InstanceDetail>> serviceDiscovery = new ConcurrentHashMap<>(4);

    ZkServiceConsumer(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    /**
     * 获取总共连接数
     */
    public int getTotalCount(String serviceName) throws Exception {
        String prePath = SLASH + serviceName;
        int count = 0;
        for (String name : this.curatorFramework.getChildren().forPath(prePath)) {
            for (String str : this.curatorFramework.getChildren().forPath(prePath + SLASH + name)) {
                if (str.startsWith(COUNT)) {
                    byte[] bytes = this.curatorFramework.getData().forPath(prePath + SLASH + name + SLASH + str);
                    count += ByteUtils.toInt(bytes);
                }
            }

        }

        return count;
    }


    /**
     * 获取负载量最小的服务(耗时严重 150ms 无法接受，后续优化)
     */
    public ServiceInstance<InstanceDetail> getMinServiceInstanceByDiscorvery(String serviceName) throws Exception {
        int min = Integer.MAX_VALUE;
        String selectedInstanceName = null;
        String selectedServiceName = null;
        String prePath = SLASH + serviceName;
        boolean change = false;
        for (String name : this.curatorFramework.getChildren().forPath(prePath)) {
            String temp = null;
            for (String str : this.curatorFramework.getChildren().forPath(prePath + SLASH + name)) {
                if (str.startsWith(COUNT)) {
                    byte[] bytes = this.curatorFramework.getData().forPath(prePath + SLASH + name + SLASH + str);
                    int curCount = ByteUtils.toInt(bytes);
                    if (min > curCount) {
                        change = true;
                        min = curCount;
                    }
                } else {
                    if (change) {
                        if (temp == null) {
                            temp = str;
                        }
                        selectedInstanceName = temp;
                        selectedServiceName = name;
                        change = false;
                    } else {
                        temp = str;
                    }
                }

                if (change && temp != null) {
                    selectedInstanceName = temp;
                    selectedServiceName = name;
                    change = false;
                }
            }
        }
        if (selectedInstanceName == null) {
            return null;
        }

        return checkAndGetServiceDiscorvery(serviceName).queryForInstance(selectedServiceName, selectedInstanceName);
    }

    public void shutdown() {
        this.serviceDiscovery.values().forEach(k -> {
            try {
                k.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    private ServiceDiscovery<InstanceDetail> checkAndGetServiceDiscorvery(String serviceName) throws Exception {
        ServiceDiscovery<InstanceDetail> discovery = null;
        if ((discovery = serviceDiscovery.get(serviceName)) == null) {
            discovery = ServiceDiscoveryBuilder.builder(InstanceDetail.class)
                    .basePath(serviceName)
                    .serializer(new JsonInstanceSerializer<>(InstanceDetail.class))
                    .client(this.curatorFramework)
                    .build();

            // 处理并发
            ServiceDiscovery<InstanceDetail> old = this.serviceDiscovery.putIfAbsent(serviceName, discovery);
            if (old != null) {
                discovery = old;
            } else {
                discovery.start();
            }
        }

        return discovery;
    }


}
