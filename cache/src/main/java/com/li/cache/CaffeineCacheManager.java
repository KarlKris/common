package com.li.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.li.persist.IEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 *
 * 基于caffeine框架实现的本地缓存
 */
public class CaffeineCacheManager<PK extends Comparable<PK> & Serializable
        , T extends IEntity<PK>> implements CacheManager<PK, T> {

    /** caffeine缓存 **/
    private Cache<PK, T> cache;

    /** 回写数据库队列 **/

    public void initCaffeineCache(int maxCount) {
        cache = Caffeine.newBuilder()
                // 最后一次访问或写入后经过固定时间过期
                .expireAfterAccess(10, TimeUnit.MINUTES)
                // 存储的最大数量
                .maximumSize(maxCount)
                // 移除回调
                .removalListener(new RemovalListener<PK, T>() {
                    @Override
                    public void onRemoval(@Nullable PK key, @Nullable T value, @NonNull RemovalCause cause) {
                        if (cause == RemovalCause.REPLACED) {
                            return;
                        }
                        // todo 加入回写数据库队列
                    }
                })
                .build();
    }

    @Override
    public T put(PK id, T entity) {
        return null;
    }

    @Override
    public T load(PK id) {
        return cache.getIfPresent(id);
    }
}
