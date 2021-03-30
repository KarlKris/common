package com.li.node;

import lombok.Getter;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/27 16:57
 * @Description: 服务信息
 **/
@Getter
public class InstanceDetail {

    /**
     * 服务器标识
     */
    private String server;

    /**
     * 服务地址
     */
    private String address;

    public InstanceDetail() {
    }

    public InstanceDetail(String server, String address) {
        this.server = server;
        this.address = address;
    }
}
