package com.icestudyroom_email.domain.rankingContract.forTest;

import com.icestudyroom_email.domain.contract.ranking.RankingChangedEvent;
import com.icestudyroom_email.domain.contract.ranking.RankingEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RankingEventTestPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void publish() {

        RankingChangedEvent event = new RankingChangedEvent(
                UUID.randomUUID().toString(),
                RankingEventType.TOP5_RANK_CHANGED,
                "2026-W08",
                6L,
                "테스트 유저 박다영",
                "pdayoung0402@hufs.ac.kr",
                4,
                6,
                1320,
                50
        );

        kafkaTemplate.send("RANKING_USER_CHANGED_EVENT", event);
    }
}
