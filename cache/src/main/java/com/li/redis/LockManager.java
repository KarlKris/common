package com.li.redis;

/**
 * redis加锁 管理接口
 **/
public interface LockManager {

    /**
     * 对key进行加锁
     *
     * @param key
     * @return 是否加锁成功
     */
    boolean lock(String key);

    /**
     * 对key进行解锁
     *
     * @param key
     */
    void unlock(String key);
}
