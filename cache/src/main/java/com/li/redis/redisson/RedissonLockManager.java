package com.li.redis.redisson;

import com.li.redis.LockManager;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @Description Redis Redisson 客户端，用于分布式锁
 * @Author li-yuanwen
 * @Date 2021/4/8 16:32
 */
public class RedissonLockManager implements LockManager {

    /** redission client **/
    private RedissonClient client;

    public RedissonLockManager(RedissonClient client) {
        this.client = client;
    }

    private RBucket<Object> getRedisBucket(String key) {
        return this.client.getBucket(key);
    }

    private RLock getLock(String key) {
        return this.client.getLock(key);
    }


    @Override
    public boolean lock(String key) {
        return getLock(key).tryLock();
    }

    @Override
    public void unlock(String key) {
        getLock(key).unlock();
    }
}
