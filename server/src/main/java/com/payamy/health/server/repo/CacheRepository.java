package com.payamy.health.server.repo;

public interface CacheRepository {

    void putAccessToken(String token, String userId);
    String getUserIdByAccessToken(String token);
}
