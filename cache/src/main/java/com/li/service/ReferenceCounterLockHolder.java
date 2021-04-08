package com.li.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 带引用计数的锁容器
 * @Author li-yuanwen
 * @Date 2021/4/8 17:01
 */
public class ReferenceCounterLockHolder<PK> {


    private static class ReferenceCounterCounterLock {
        // 锁对象
        private ReentrantLock lock = new ReentrantLock();
        // 计数器
        private AtomicInteger refs = new AtomicInteger();

        /**
         * 释放锁, 并减少锁的获取计数
         */
        public int release(ReentrantLock lock) {
            if (this.lock != lock) {
                return refs.get();
            }
            return refs.decrementAndGet();
        }

        /**
         * 获取锁, 并增加锁的获取计数
         */
        public ReentrantLock acquire() {
            refs.incrementAndGet();
            return lock;
        }
    }

    // 锁集合
    private Map<PK, ReferenceCounterCounterLock> locks = new HashMap<PK, ReferenceCounterCounterLock>();

    /**
     * 释放锁引用 - 计数器-1
     */
    synchronized public void release(PK id, ReentrantLock lock) {
        ReferenceCounterCounterLock warp = locks.get(id);
        if (warp.release(lock) <= 0) {
            locks.remove(id, warp);
        }
    }

    /**
     * 获取锁引用 - 计数器+1
     */
    synchronized public ReentrantLock acquire(PK id) {
        ReferenceCounterCounterLock warp = locks.get(id);
        if (warp == null) {
            warp = new ReferenceCounterCounterLock();
            ReferenceCounterCounterLock prev = locks.putIfAbsent(id, warp);
            warp = prev != null ? prev : warp;
        }
        ReentrantLock lock = warp.acquire();
        return lock;
    }

    /**
     * 清除全部锁引用
     */
    synchronized public void clear() {
        locks.clear();
    }
}
