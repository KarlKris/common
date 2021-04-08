package com.li.service;

import com.alibaba.fastjson.JSON;
import com.li.persist.IEntity;
import com.li.redis.CacheManager;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 缓存数据Service
 * @Author li-yuanwen
 * @Date 2021/4/7 16:55
 */
public class CacheServiceImpl<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> implements CacheService<PK, T> {


    private CacheManager cacheManager;

    /** 主键锁 **/
    private ReferenceCounterLockHolder<PK> locks = new ReferenceCounterLockHolder<>();

    @Override
    public T load(PK id) {
        Object obj = cacheManager.get(JSON.toJSONString(id));
        if (obj != null) {
            return (T) obj;
        }

        // 将主键锁起，防止并发操作
        ReentrantLock lock = lockPkLock(id);
        try{
            // todo 从数据库查询

            return null;
        }finally {
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
        lock.unlock();
        locks.release(id, lock);
    }

    /** 获取主键锁对象 */
    private ReentrantLock lockPkLock(PK id) {
        ReentrantLock lock = locks.acquire(id);
        lock.lock();
        return lock;
    }
}
