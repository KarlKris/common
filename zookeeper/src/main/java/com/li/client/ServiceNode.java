package com.li.client;

import lombok.Getter;

/**
 * @Description Service Node 节点信息
 * @Author li-yuanwen
 * @Date 2021/3/30 20:33
 */
@Getter
public class ServiceNode {

    /**
     * 子版本号
     **/
    private final int cversion;

    /**
     * 最小负载量ServiceInstanceName
     **/
    private final String instanceName;

    public ServiceNode(int cversion, String instanceName) {
        this.cversion = cversion;
        this.instanceName = instanceName;
    }
}
