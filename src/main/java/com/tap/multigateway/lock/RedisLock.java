package com.tap.multigateway.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLock {

    private final StringRedisTemplate stringRedisTemplate;

    public Boolean lock(final String key, final Long timeout) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, "LOCK", timeout, TimeUnit.SECONDS);
    }

    public void unlock(final String key) {
        stringRedisTemplate.delete(key);
    }
}