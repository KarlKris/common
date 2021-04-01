package com.li.node;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.li.consumer.ZkServiceConsumer.SERIALIZER;
import static com.li.provider.ZkServiceProvider.SERVICE_DISCORVERY_SUFFIX;

/**
 * @Description 服务发现节点
 * @Author li-yuanwen
 * @Date 2021/4/1 19:52
 */
public class ServiceDiscoveryNode {

    /**
     * 服务名称
     **/
    private String serviceName;

    /**
     * 服务发现
     **/
    private ServiceDiscovery<InstanceDetail> discovery;

    /**
     * 服务缓存
     */
    private ServiceCache<InstanceDetail> cache;

    /**
     * 服务payload实例缓存
     **/
    private ConcurrentHashMap<String, InstanceDetail> instances = new ConcurrentHashMap<>();

    public ServiceDiscoveryNode(String serviceName) {
        this.serviceName = serviceName;
    }

    public void start(CuratorFramework curatorFramework) throws Exception {
        if (this.discovery != null) {
            return;
        }
        this.discovery = ServiceDiscoveryBuilder.builder(InstanceDetail.class)
                .client(curatorFramework)
                .basePath(serviceName + SERVICE_DISCORVERY_SUFFIX)
                .serializer(SERIALIZER)
                .build();

        this.discovery.start();
        this.cache = discovery.serviceCacheBuilder().name(serviceName).build();

        this.cache.addListener(new ServiceCacheListener() {
            @Override
            public void cacheChanged() {
                initInstance();
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

            }
        });

        this.cache.start();

        initInstance();
    }

    public ServiceInstance<InstanceDetail> getServiceInstance(String instanceName) throws Exception {
        checkDiscovery();

        return this.discovery.queryForInstance(this.serviceName, instanceName);
    }

    public InstanceDetail getInstanceDetail(String server) {
        return this.instances.get(server);
    }

    public void shutdown() throws IOException {
        if (this.cache == null) {
            return;
        }

        this.cache.close();
    }

    private void initInstance() {
        Map<String, InstanceDetail> map = new HashMap<>();
        for (ServiceInstance<InstanceDetail> instance : this.cache.getInstances()) {
            InstanceDetail detail = instance.getPayload();
            map.put(detail.getServer(), detail);
        }

        this.instances = new ConcurrentHashMap<>(map);
    }

    private void checkDiscovery() {
        if (this.discovery == null) {
            throw new UnsupportedOperationException("use before discorvery start");
        }
    }
}
