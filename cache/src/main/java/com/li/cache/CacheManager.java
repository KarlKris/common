package com.li.cache;

import com.li.persist.IEntity;

/**
 * @author li-yuanwen
 * 本地缓存管理接口
 **/
public interface CacheManager<PK extends Comparable<PK>, T extends IEntity<PK>> {

    /**
     * load缓存
     * @param id 主键id
     * @return 缓存对象
     */
    T load(PK id);

    /**
     *  put 缓存
     * @param id 主键id
     * @param entity 新实体
     * @return 新实体
     */
    T put(PK id, T entity);

}
