package com.payamy.health.server.service;

import com.payamy.health.server.entity.AccessToken;
import com.payamy.health.server.repo.AccessTokenRepository;
import com.payamy.health.server.repo.CacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Override
    public void putAccessToken(String token, Long userId) {
        // store token in the cache
        cacheRepository.putAccessToken(token, String.valueOf(userId));

        // store token in the persistence
        AccessToken accessToken = AccessToken.builder()
                .token(token)
                .userId(userId)
                .createdAt(Calendar.getInstance().getTime())
                .build();
        accessTokenRepository.save(accessToken);
    }

    @Override
    public AccessToken getAccessToken(Long userId) {
        return accessTokenRepository.findByUserId(userId);
    }
}
