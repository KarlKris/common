package com.li.client;

import lombok.Data;

/**
 * @Auther: li-yuanwen
 * @Date: 2021/3/27 16:57
 * @Description: 服务信息
 **/
@Data
public class InstanceDetail {

    /**
     * 服务地址
     */
    private String address;

    public InstanceDetail() {
    }

    public InstanceDetail(String address) {
        this.address = address;
    }
}
