package com.payamy.health.server.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisFactory {

    @Value("${cache.redis.host}")
    private static String host;

    @Value("${cache.redis.port}")
    private static Integer port;

    @Value("${cache.redis.timeout}")
    private static Integer timeout;

    @Value("${cache.redis.password}")
    private static String password;

    private static JedisFactory jedisFactory;

    // hide the constructor
    private JedisFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);

        jedisPool = new JedisPool(
                poolConfig,
                host,
                port,
                timeout,
                password
        );
    }

    private static JedisPool jedisPool;

    public static Jedis getConnection() {

        if (jedisFactory == null) {
            jedisFactory = new JedisFactory();
        }
        return jedisPool.getResource();
    }
}
