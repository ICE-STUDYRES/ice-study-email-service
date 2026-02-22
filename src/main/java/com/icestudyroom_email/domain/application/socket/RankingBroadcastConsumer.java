package com.icestudyroom_email.domain.application.socket;

import com.icestudyroom_email.domain.contract.ranking.RankingEmailEvent;
import com.icestudyroom_email.domain.contract.ranking.RankingEventType;
import com.icestudyroom_email.domain.infrastructure.socket.broadcaster.SocketRankingBroadcaster;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "feature.kafka.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class RankingBroadcastConsumer {

    private final SocketRankingBroadcaster SocketRankingBroadcaster;

    @PostConstruct
    public void init() {
        log.info("[RankingBroadcast] RankingBroadcastConsumer initialized");
    }

    @KafkaListener(
            topics = "RANKING_EMAIL_EVENT",
            groupId = "ranking-broadcast-group",
            containerFactory = "rankingKafkaListenerContainerFactory"
    )
    public void consume(RankingEmailEvent event, Acknowledgment ack) {

        try {
            if (event.eventType() == RankingEventType.TOP5_RANK_CHANGED) {

                log.info(
                        "[RankingBroadcast] TOP5 changed. broadcasting. memberId={}",
                        event.memberId()
                );

                SocketRankingBroadcaster.broadcastToAll(
                        "ranking-update",
                        event
                );
            }

        } catch (Exception e) {
            log.error("[RankingBroadcast] error", e);
        } finally {
            ack.acknowledge();
        }
    }
}
