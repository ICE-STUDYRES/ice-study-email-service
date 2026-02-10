package com.icestudyroom_email.domain.rankingContract.forTest;

import com.icestudyroom_email.domain.contract.ranking.RankingEmailEvent;
import com.icestudyroom_email.domain.contract.ranking.RankingEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RankingEventTestPublisher {

    private final KafkaTemplate<String, RankingEmailEvent> kafkaTemplate;

    public void publishTop5EnterEvent() {
        RankingEmailEvent event = new RankingEmailEvent(
                "121235",
                RankingEventType.TOP6_10_RANK_CHANGED,
                1L,
                "테스트 유저 박다영",
                "forTestRanking@gmail.com",
                5,
                6,
                10);


        kafkaTemplate.send("RANKING_EMAIL_EVENT", event);

    }

    public void publishPersonalNotificationEvent(Long memberId) {

        RankingEmailEvent event = new RankingEmailEvent(
                UUID.randomUUID().toString(),
                RankingEventType.TOP5_RANK_CHANGED,
                memberId,
                "test-user",
                "test@test.com",
                3,
                5,
                2
        );

        kafkaTemplate.send("RANKING_EMAIL_EVENT", event);
    }

}
