package com.li.redis;

import java.util.Set;

/**
 * @Description 缓存操作管理接口
 * @Author li-yuanwen
 * @Date 2021/4/7 20:49
 */
public interface CacheManager {


    /**
     * 根据key获取对象
     *
     * @param key
     * @return
     */
    Object get(final String key);

    /**
     * 根据正则表达式获取对象
     *
     * @param pattern 正则表达式
     * @return
     */
    Set<Object> getAll(final String pattern);

    /**
     * 设置key-value
     *
     * @param key
     * @param value
     * @param seconds 过期时间(秒)
     */
    void set(final String key, final Object value, int seconds);

    /**
     * 设置key-value 过期时间使用默认配置值
     *
     * @param key
     * @param value
     */
    void set(final String key, final Object value);

    /**
     * 根据key判断某一对象是否存在
     *
     * @param key
     * @return 是否存在
     */
    Boolean exists(final String key);

    /**
     * 根据key删除对象
     *
     * @param key
     */
    void del(final String key);

    /**
     * 根据正则表达式删除对象
     *
     * @param pattern 正则表达式
     * @return
     */
    void delAll(final String pattern);

    /**
     * 根据key获取对应对象的类型
     *
     * @param key
     * @return 对应对象的类型
     */
    String type(final String key);

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param seconds
     * @return 是否设置成功
     */
    Boolean expire(final String key, final int seconds);

    /**
     * 设置key在指定时间点后过期
     *
     * @param key
     * @param unixTime
     * @return 是否成功
     */
    Boolean expireAt(final String key, final long unixTime);

    /**
     * 获取对应key的过期时间
     *
     * @param key
     * @return
     */
    Long ttl(final String key);

    /**
     * 设置新值并返回旧值
     *
     * @param key
     * @param value
     * @return 旧值
     */
    Object getSet(final String key, final Object value);


    /**
     * 根据key设置对应哈希表对象的field - value
     *
     * @param key
     * @param field
     * @param value
     */
    void hset(String key, Object field, Object value);

    /**
     * 根据key获取对应哈希表的对应field的对象
     *
     * @param key
     * @param field
     * @return
     */
    Object hget(String key, Object field);

    /**
     * 根据key删除对应哈希表的对应field的对象
     *
     * @param key
     * @param field
     * @return
     */
    void hdel(String key, Object field);

    /**
     * 指定的 key 不存在时,为 key 设置指定的value
     *
     * @param key
     * @param value
     * @return 是否设置成功
     */
    boolean setnx(String key, Object value);

    /**
     * 对应key的值自增
     *
     * @param key
     * @return 自增后的值
     */
    Long incr(String key);

    /**
     * 用指定的字符串覆盖给定 key 所储存的字符串值，覆盖的位置从偏移量 offset 开始
     *
     * @param key
     * @param offset 偏移量
     * @param value
     */
    void setrange(String key, long offset, String value);

    /**
     * 用于获取存储在指定 key 中字符串的子字符串。字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。
     *
     * @param key
     * @param startOffset
     * @param endOffset
     * @return
     */
    String getrange(String key, long startOffset, long endOffset);

    /**
     * 将value设置至指定key的set集合中
     *
     * @param key
     * @param value
     */
    void sadd(String key, Object value);

    /**
     * 获取指定key的set集合
     *
     * @param key
     * @return
     */
    Set<?> sall(String key);

    /**
     * 删除指定key的set集合中的value
     *
     * @param key
     * @param value
     * @return
     */
    boolean sdel(String key, Object value);
}
