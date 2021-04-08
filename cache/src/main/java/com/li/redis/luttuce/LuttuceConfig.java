package com.li.redis.luttuce;

import io.lettuce.core.RedisURI;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description luttuce 配置文件读取
 * @Author li-yuanwen
 * @Date 2021/4/8 11:43
 */
@PropertySource(value = "classpath:luttuce.properties")
public class LuttuceConfig {

    /** redis集群地址分隔符 **/
    public static final String SEPARATOR = ";";

    /** redis集群地址 ;分隔 **/
    @Value("redis.luttuce.urls:")
    private String urls;

    /** redis 键值有效时间(秒) **/
    @Value("redis.luttuce.expire.second:")
    private int expireSecond;

    public List<RedisURI> getRedisClusterUri() {
        if (StringUtils.isEmpty(urls)) {
            return null;
        }
        List<RedisURI> redisRedisList = new ArrayList<>();
        for (String url : urls.split(SEPARATOR)) {
            redisRedisList.add(RedisURI.create(url));
        }

        return redisRedisList;
    }

    public int getExpireSecond() {
        return expireSecond;
    }
}
