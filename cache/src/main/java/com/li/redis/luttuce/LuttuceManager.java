package com.li.redis.luttuce;

import com.alibaba.fastjson.JSON;
import com.li.redis.CacheManager;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description Redis Luttuce客户端 缓存管理接口
 * @Author li-yuanwen
 * @Date 2021/4/8 10:55
 */
public class LuttuceManager implements CacheManager {

    /**
     * luttuce配置对象
     **/
    private LuttuceConfig config;

    /**
     * redis cluster
     **/
    private RedisClusterClient client;
    /**
     * 连接
     **/
    private StatefulRedisClusterConnection<String, String> connection;

    public LuttuceManager(RedisClusterClient client, LuttuceConfig config) {
        this.client = client;
        this.connection = this.client.connect();

        this.config = config;
    }

    private RedisAdvancedClusterCommands<String, String> getSyncCommand() {
        return this.connection.sync();
    }

    private RedisAdvancedClusterReactiveCommands<String, String> getReactiveCommand() {
        return this.connection.reactive();
    }

    public void shutdown() {
        if (connection != null) {
            connection.close();
        }
        client.shutdown();
    }

    // ---------------------- CacheManager实现 -------------------------------

    @Override
    public Object get(String key) {
        return getSyncCommand().get(key);
    }

    @Override
    public Set<Object> getAll(String pattern) {
        RedisAdvancedClusterCommands<String, String> commands = getSyncCommand();
        List<String> keys = commands.keys(pattern);
        Set<Object> objs = new HashSet<>(keys.size());
        for (String key : keys) {
            objs.add(commands.get(key));
        }
        return objs;
    }

    @Override
    public void set(String key, Object value, int seconds) {
        RedisAdvancedClusterCommands<String, String> commands = getSyncCommand();
        commands.set(key, JSON.toJSONString(value));
        commands.expire(key, seconds);
    }

    @Override
    public void set(String key, Object value) {
        RedisAdvancedClusterCommands<String, String> commands = getSyncCommand();
        commands.set(key, JSON.toJSONString(value));
        commands.expire(key, config.getExpireSecond());
    }

    @Override
    public Boolean exists(String key) {
        Long exists = getSyncCommand().exists(key);
        return exists == 1;
    }

    @Override
    public void del(String key) {
        getSyncCommand().del(key);
    }

    @Override
    public void delAll(String pattern) {
        RedisAdvancedClusterCommands<String, String> commands = getSyncCommand();
        commands.del(commands.keys(pattern).toArray(new String[0]));
    }

    @Override
    public String type(String key) {
        return getSyncCommand().type(key);
    }

    @Override
    public Boolean expire(String key, int seconds) {
        return getSyncCommand().expire(key, seconds);
    }

    @Override
    public Boolean expireAt(String key, long unixTime) {
        return getSyncCommand().expireat(key, unixTime);
    }

    @Override
    public Long ttl(String key) {
        return getSyncCommand().ttl(key);
    }

    @Override
    public Object getSet(String key, Object value) {
        return getSyncCommand().getset(key, JSON.toJSONString(value));
    }

    @Override
    public void hset(String key, Object field, Object value) {
        getSyncCommand().hset(key, JSON.toJSONString(field), JSON.toJSONString(value));
    }

    @Override
    public Object hget(String key, Object field) {
        return getSyncCommand().hget(key, JSON.toJSONString(field));
    }

    @Override
    public void hdel(String key, Object field) {
        getSyncCommand().hdel(key, JSON.toJSONString(field));
    }

    @Override
    public boolean setnx(String key, Object value) {
        return getSyncCommand().setnx(key, JSON.toJSONString(value));
    }

    @Override
    public Long incr(String key) {
        return getSyncCommand().incr(key);
    }

    @Override
    public void setrange(String key, long offset, String value) {
        getSyncCommand().setrange(key, offset, value);
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return getSyncCommand().getrange(key, startOffset, endOffset);
    }

    @Override
    public void sadd(String key, Object value) {
        getSyncCommand().sadd(key, JSON.toJSONString(value));
    }

    @Override
    public Set<?> sall(String key) {
        return getSyncCommand().smembers(key);
    }

    @Override
    public boolean sdel(String key, Object value) {
        return getSyncCommand().srem(key, JSON.toJSONString(value)) == 1;
    }

}
