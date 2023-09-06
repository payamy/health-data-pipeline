package com.payamy.health.server.repo;

import com.payamy.health.server.cache.JedisFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class CacheRepositoryImpl implements CacheRepository {

    @Override
    public void putAccessToken(String token, String userId) {

        try (Jedis jedis = JedisFactory.getConnection()) {

            jedis.set(token, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUserIdByAccessToken(String token) {

        try (Jedis jedis = JedisFactory.getConnection()) {

            return jedis.get(token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
