package com.icestudyroom_email.domain.infrastructure.redis.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisIdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public boolean isFirst(String key, Duration timeout) {
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", timeout);
        return Boolean.TRUE.equals(result);

    }
}
