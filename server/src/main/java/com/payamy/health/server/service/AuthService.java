package com.payamy.health.server.service;

import com.payamy.health.server.entity.AccessToken;

public interface AuthService {
    void putAccessToken(String token, Long userId);
    AccessToken getAccessToken( Long userId);
}
