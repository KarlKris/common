package com.li.service;

import com.li.cache.CacheManager;
import com.li.cache.ReferenceCounterLockHolder;
import com.li.persist.Accessor;
import com.li.persist.IEntity;
import com.li.persist.WriteBackElement;
import com.li.persist.WriteBackManager;
import com.li.persist.WriteBackType;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  缓存数据Service
 * @author li-yuanwen
 * @date 2021/4/7 16:55
 */
public class CacheServiceImpl<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>>
        implements CacheService<PK, T> {

    /** 实体类型 */
    private Class<T> entityClz;

    /** Caffeine缓存 **/
    private CacheManager<PK, T> cacheManager;

    /** 数据库访问器 **/
    private Accessor accessor;

    /** 数据回写 **/
    private WriteBackManager writeBackManager;

    /** 主键锁 **/
    private ReferenceCounterLockHolder<PK> locks = new ReferenceCounterLockHolder<>();

    @Override
    public T load(PK id) {
        T entity = cacheManager.load(id);
        if (entity != null) {
            return entity;
        }

        // 将主键锁起，防止并发操作
        ReentrantLock lock = lockPkLock(id);
        lock.lock();
        try {
            // 从数据库查询
            entity = accessor.load(id, entityClz);
            // 加入缓存
            return cacheManager.put(id, entity);
        } finally {
            lock.unlock();
            releasePkLock(id, lock);
        }
    }

    @Override
    public void writeBack(T t) {
        writeBackManager.put(new WriteBackElement(WriteBackType.UPDATE, t));
    }

    @Override
    public void remove(PK id) {
        T t = load(id);
        if (t == null) {
            return;
        }
        writeBackManager.put(new WriteBackElement(WriteBackType.REMOVE, t));
    }

    // --------------------- 私有方法 ---------------------------

    /** 释放主键锁 */
    private void releasePkLock(PK id, ReentrantLock lock) {
        locks.release(id, lock);
    }

    /** 获取主键锁对象 */
    private ReentrantLock lockPkLock(PK id) {
        return locks.acquire(id);
    }
}
