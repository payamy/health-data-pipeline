package com.payamy.health.server.repo;

import com.payamy.health.server.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    AccessToken findByUserId(Long userId);
    AccessToken findByToken(String token);
    void deleteByUserId(Long userId);
}
