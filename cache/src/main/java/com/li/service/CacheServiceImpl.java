package com.li.service;

import com.li.cache.CacheManager;
import com.li.cache.ReferenceCounterLockHolder;
import com.li.persist.IEntity;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  缓存数据Service
 * @author li-yuanwen
 * @date 2021/4/7 16:55
 */
public class CacheServiceImpl<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>>
        implements CacheService<PK, T> {

    private CacheManager<PK, T> cacheManager;

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
            // todo 从数据库查询

            return null;
        } finally {
            lock.unlock();
            releasePkLock(id, lock);
        }

    }

    @Override
    public void writeBack(T t) {

    }

    @Override
    public T remove(PK id) {
        return null;
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
