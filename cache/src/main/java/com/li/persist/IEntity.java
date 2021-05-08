package com.li.persist;

/**
 * 持久化实体基类
 *
 * @author li-yuanwen
 **/
public interface IEntity<PK> {

    /**
     * 返回主键
     *
     * @return 返回分布式主键
     **/
    PK getId();
}