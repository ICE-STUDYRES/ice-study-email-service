package com.icestudyroom_email.domain.socket.infrastructure.ranking;

import com.icestudyroom_email.domain.rankingContract.RankingPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingQueryService {

    private final StringRedisTemplate redisTemplate;

    public Set<ZSetOperations.TypedTuple<String>> getRankingTuples(
            RankingPeriod period
    ) {
        RedisConnectionFactory connectionFactory =
                redisTemplate.getConnectionFactory();

        if (connectionFactory instanceof LettuceConnectionFactory lettuce) {
            log.info("[RedisDebug] Redis host={}, port={}, db={}",
                    lettuce.getHostName(),
                    lettuce.getPort(),
                    lettuce.getDatabase());
        }

        Set<ZSetOperations.TypedTuple<String>> rankingItems =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(
                                period.getRedisKey(),
                                0,
                                period.getLimit() - 1
                        );

        if (rankingItems == null || rankingItems.isEmpty()) {
            log.info("[RedisDebug] key={}, EMPTY", period.getRedisKey());
            return Set.of();
        }

        rankingItems.forEach(t ->
                log.info("[RedisDebug] member={}, score={}",
                        t.getValue(),
                        t.getScore())
        );

        return rankingItems;
    }

}
