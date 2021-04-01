package com.li.node;

import lombok.Getter;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;

import java.io.IOException;

/**
 * @Description Service Node 节点信息
 * @Author li-yuanwen
 * @Date 2021/3/30 20:33
 */
@Getter
public class ServiceNode {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 对应连接计数节点数据版本号
     **/
    private int version;

    /**
     * 最小负载量ServiceInstanceName
     **/
    private String instanceName;


    public ServiceNode(String serviceName, int version, String instanceName) {
        this.version = version;
        this.instanceName = instanceName;
        this.serviceName = serviceName;
    }



}
