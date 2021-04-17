package com.li.consumer;

import com.li.common.ByteUtils;
import com.li.node.InstanceDetail;
import com.li.node.ServiceDiscoveryNode;
import com.li.node.ServiceNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.li.provider.ZkServiceProvider.*;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 15:48
 * @Description: zookeeper 服务使用者
 **/
public class ZkServiceConsumer {

    public static final JsonInstanceSerializer<InstanceDetail> SERIALIZER = new JsonInstanceSerializer<>(InstanceDetail.class);

    /**
     * zookeeper 客户端
     */
    private CuratorFramework curatorFramework;

    /**
     * 服务发现
     */
    private ConcurrentHashMap<String, ServiceDiscoveryNode> serviceDiscovery = new ConcurrentHashMap<>(4);

    /**
     * 服务节点信息
     **/
    private ConcurrentHashMap<String, ServiceNode> serviceVersion = new ConcurrentHashMap<>(4);


    ZkServiceConsumer(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    /**
     * 获取总共连接数
     */
    public int getTotalCount(String serviceName) throws Exception {
        String prePath = mkServiceCountPrePath(serviceName);
        int count = 0;
        for (String name : this.curatorFramework.getChildren().forPath(prePath)) {
            byte[] bytes = this.curatorFramework.getData().forPath(prePath + SLASH + name);
            count += ByteUtils.toInt(bytes);
        }
        return count;
    }


    /**
     * 获取负载量最小的服务
     */
    public ServiceInstance<InstanceDetail> getMinServiceInstance(String serviceName) throws Exception {
        String selectedInstanceName = getMinServiceCountInstanceName(serviceName);
        if (selectedInstanceName == null) {
            return null;
        }

        return checkAndGetServiceDiscorvery(serviceName).getServiceInstance(selectedInstanceName);
    }

    public void shutdown() {
        this.serviceDiscovery.values().forEach(k -> {
            try {
                k.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    private ServiceDiscoveryNode checkAndGetServiceDiscorvery(String serviceName) throws Exception {
        ServiceDiscoveryNode discoveryNode = null;
        if ((discoveryNode = serviceDiscovery.get(serviceName)) == null) {
            discoveryNode = new ServiceDiscoveryNode(serviceName);

            // 处理并发
            ServiceDiscoveryNode old = this.serviceDiscovery.putIfAbsent(serviceName, discoveryNode);
            if (old != null) {
                discoveryNode = old;
            } else {
                discoveryNode.start(this.curatorFramework);
            }
        }

        return discoveryNode;
    }


    /**
     * 构建服务发现根路径
     **/
    private String mkServiceDiscorveryPrePath(String serviceName) {
        return SLASH + serviceName + SERVICE_DISCORVERY_SUFFIX + SLASH + serviceName;
    }

    /**
     * 构建服务连接数根路径
     **/
    private String mkServiceCountPrePath(String serviceName) {
        return mkServiceDiscorveryPrePath(serviceName) + COUNT;
    }


    private String getMinServiceCountInstanceName(String serviceName) throws Exception {
        // 对比版本
        Stat stat = new Stat();
        curatorFramework.getData().storingStatIn(stat).forPath(mkServiceCountPrePath(serviceName));

        ServiceNode node = serviceVersion.get(serviceName);
        int version = stat.getVersion();
        // 版本不一致
        if (node == null || node.getVersion() != version) {
            String selectedInstanceName = doSearchMinCountServiceInstanceName(serviceName);
            serviceVersion.put(serviceName, new ServiceNode(serviceName, version, selectedInstanceName));
            return selectedInstanceName;
        }
        return node.getInstanceName();

    }


    private String doSearchMinCountServiceInstanceName(String serviceName) throws Exception {
        int min = Integer.MAX_VALUE;
        String selectedInstanceName = null;
        String path = mkServiceCountPrePath(serviceName);
        for (String name : this.curatorFramework.getChildren().forPath(path)) {
            byte[] bytes = this.curatorFramework.getData().forPath(path + SLASH + name);
            int curCount = ByteUtils.toInt(bytes);
            if (min > curCount) {
                min = curCount;
                selectedInstanceName = name;
            }
        }
        if (selectedInstanceName == null) {
            return null;
        }

        return selectedInstanceName;
    }
}
