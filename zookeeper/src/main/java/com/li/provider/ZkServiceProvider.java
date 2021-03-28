package com.li.provider;

import com.li.client.CuratorFrameworkFactoryBean;
import com.li.client.InstanceDetail;
import com.li.common.ByteUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/28 15:15
 * @Description: 服务注册
 **/
public class ZkServiceProvider {

    public static final String SLASH = "/";

    /**
     * 连接数
     */
    public static final String COUNT = "count_record";


    /**
     * zookeeper curator #ServiceDisCorvery
     */
    private ServiceDiscovery<InstanceDetail> serviceDiscovery;

    /**
     * zookeeper client
     */
    private CuratorFramework curatorFramework;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 连接数节点路径
     */
    private String countPath;

    ZkServiceProvider(CuratorFramework curatorFramework, String serviceName) throws Exception {
        this.curatorFramework = curatorFramework;
        this.serviceName = serviceName;

        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetail.class)
                .basePath(serviceName)
                .serializer(new JsonInstanceSerializer<>(InstanceDetail.class))
                .client(this.curatorFramework)
                .build();

        this.serviceDiscovery.start();

    }


    /**
     * 服务注册
     *
     * @param ip   ip地址
     * @param port 服务端口号
     */
    public void registerService(String ip, int port, InstanceDetail instance) throws Exception {
        checkServiceRegisterOrNot();

        String name = makeCountPathName(serviceName, ip, port);
        ServiceInstance<InstanceDetail> serviceInstance = ServiceInstance.<InstanceDetail>builder()
                .address(ip)
                .port(port)
                .name(name)
                .payload(instance).build();

        this.serviceDiscovery.registerService(serviceInstance);

        // 创建连接数节点
        this.countPath = createCountNode(name);
    }

    /**
     * 更新连接服务的连接数
     */
    public void updateCount(int count) throws Exception {
        if (!isServiceRegister()) {
            throw new UnsupportedOperationException("service not register in zookeeper");
        }

        this.curatorFramework.setData().forPath(countPath, ByteUtils.toByteArray(count));

    }

    /**
     * 关闭
     */
    public void shutdown() throws IOException {
        this.curatorFramework.close();
        this.serviceDiscovery.close();
    }

    // 私有方法

    /**
     * 创建记录连接数的临时节点
     */
    private String createCountNode(String path) throws Exception {
        // 临时的节点
        return this.curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(SLASH + serviceName + SLASH + path + SLASH + COUNT, ByteUtils.toByteArray(0));
    }

    private void checkServiceRegisterOrNot() {
        if (isServiceRegister()) {
            throw new UnsupportedOperationException("already register service!");
        }
    }

    private boolean isServiceRegister() {
        return this.countPath != null;
    }

    private String makeCountPathName(String serviceName, String ip, int port) {
        return serviceName + "-" + ip + "-" + port;
    }


}
