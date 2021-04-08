package com.li.service;

import com.li.persist.IEntity;

import java.io.Serializable;

/**
 * 缓存数据Service
 *
 * @author li-yuanwen
 **/
public interface CacheService<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> {

    /**
     * 读取数据
     *
     * @param id 主键
     * @return T 实体
     **/
    T load(PK id);

    /**
     * 回写数据
     *
     * @param t 回写实体
     **/
    void writeBack(T t);

    /**
     * 移除实体(持久化层面)
     *
     * @param id 主键
     * @return T 移除的实体
     **/
    T remove(PK id);
}
