package com.li.persist;

import java.io.Serializable;

/**
 * @author li-yuanwen
 *
 * 数据层访问管理器
 */
public interface Accessor {

    /**
     *  访问数据库
     * @param id 主键id
     * @param tClass 实体对象class
     * @param <PK> 主键
     * @param <T> 实体类型
     * @return 数据库表某行数据对应实体对象
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass);

    /**
     *
     * @param t 需要移除的实体
     * @param <PK> 主键
     * @param <T>  实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void remove(T t);

    /**
     *
     * @param t 需要更新的实体
     * @param <PK> 主键
     * @param <T> 更新后的实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void update(T t);

    /**
     *
     * @param t 新创建的实体
     * @param <PK> 主键
     * @param <T> 实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void create(T t);

}
