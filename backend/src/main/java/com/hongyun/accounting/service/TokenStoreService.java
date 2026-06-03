package com.hongyun.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenStoreService {
    private static final String USER_KEY_PREFIX = "jwt:user:";
    private static final String JTI_KEY_PREFIX = "jwt:jti:";

    @Autowired
    private StringRedisTemplate redis;

    public void storeLoginToken(String username, String jti, Duration ttl) {
        String oldJti = redis.opsForValue().get(USER_KEY_PREFIX + username);
        if (oldJti != null && !oldJti.isBlank()) {
            redis.delete(JTI_KEY_PREFIX + oldJti);
        }

        redis.opsForValue().set(JTI_KEY_PREFIX + jti, username, ttl);
        redis.opsForValue().set(USER_KEY_PREFIX + username, jti, ttl);
    }

    public boolean isTokenValid(String username, String jti) {
        if (username == null || username.isBlank() || jti == null || jti.isBlank()) return false;
        String expectedJti = redis.opsForValue().get(USER_KEY_PREFIX + username);
        if (expectedJti == null || !expectedJti.equals(jti)) return false;
        return Boolean.TRUE.equals(redis.hasKey(JTI_KEY_PREFIX + jti));
    }

    public void revokeToken(String username, String jti) {
        if (jti != null && !jti.isBlank()) {
            redis.delete(JTI_KEY_PREFIX + jti);
        }
        if (username != null && !username.isBlank()) {
            redis.delete(USER_KEY_PREFIX + username);
        }
    }
}
