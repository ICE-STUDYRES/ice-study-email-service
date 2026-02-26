package com.icestudyroom_email.domain.application.socket;

import com.icestudyroom_email.domain.contract.ranking.RankingListUpdatedEvent;
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
            topics = "RANKING_LIST_UPDATED_EVENT",
            groupId = "ranking-broadcast-group",
            containerFactory = "rankingListKafkaListenerContainerFactory"
    )
    public void consume(RankingListUpdatedEvent event, Acknowledgment ack) {

        try {
            log.info("[RankingBroadcast] broadcasting ranking list. periodKey={}",
                    event.periodKey());

            SocketRankingBroadcaster.broadcastToAll(
                    "ranking-update",
                    event.rankingList()
            );

        } catch (Exception e) {
            log.error("[RankingBroadcast] error", e);
        } finally {
            ack.acknowledge();
        }
    }
}
