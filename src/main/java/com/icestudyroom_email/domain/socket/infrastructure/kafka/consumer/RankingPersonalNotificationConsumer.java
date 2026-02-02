package com.icestudyroom_email.domain.socket.infrastructure.kafka.consumer;

import com.icestudyroom_email.domain.common.redis.idempotency.RedisIdempotencyService;
import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
import com.icestudyroom_email.domain.socket.infrastructure.ranking.PersonalNotificationBroadcaster;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "feature.kafka.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class RankingPersonalNotificationConsumer {

    private final RedisIdempotencyService redisIdempotencyService;
    private final PersonalNotificationBroadcaster personalNotificationBroadcaster;

    @PostConstruct
    public void init() {
        log.info("[PersonalNotify] RankingPersonalNotificationConsumer initialized");
    }

    @KafkaListener(
            topics = "RANKING_EMAIL_EVENT",
            groupId = "personal-notification-group",
            containerFactory = "rankingKafkaListenerContainerFactory"
    )
    public void consume(RankingEmailEvent event, Acknowledgment ack) {

        String idempotencyKey = String.format(
                "notify:personal:%d",
                event.memberId()
        );

        try {
            if (!redisIdempotencyService.isFirst(idempotencyKey, Duration.ofDays(7))) {
                log.info(
                        "[PersonalNotify] duplicate ignored. memberId={}",
                        event.memberId()
                );
                ack.acknowledge();
                return;
            }

            log.info(
                    "[PersonalNotify] send personal notification. memberId={}, type={}",
                    event.memberId(),
                    event.eventType()
            );

            personalNotificationBroadcaster.send(
                    event.memberId(),
                    event.eventType().name()
            );

        } catch (Exception e) {
            log.error(
                    "[PersonalNotify] error while handling personal notification. memberId={}",
                    event.memberId(),
                    e
            );
        } finally {
            ack.acknowledge();
        }
    }
}