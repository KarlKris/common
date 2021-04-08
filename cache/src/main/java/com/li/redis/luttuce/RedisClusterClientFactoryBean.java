package com.li.redis.luttuce;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description RedisClusterClient FactoryBean
 * @Author li-yuanwen
 * @Date 2021/4/8 11:05
 */
public class RedisClusterClientFactoryBean implements FactoryBean<RedisClusterClient>, InitializingBean {

    private RedisClusterClient client;

    @Autowired
    private LuttuceConfig luttuceConfig;

    @Override
    public RedisClusterClient getObject() throws Exception {
        if (client == null) {
            throw new BeanInstantiationException(RedisClusterClient.class, "redis nodes url not found");
        }
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisClusterClient.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<RedisURI> redisClusterUri = luttuceConfig.getRedisClusterUri();
        if (CollectionUtils.isEmpty(redisClusterUri)) {
            return;
        }

        this.client = RedisClusterClient.create(redisClusterUri);
    }
}
