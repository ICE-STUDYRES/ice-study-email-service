package com.icestudyroom_email.domain.rankingContract.email;

import com.icestudyroom_email.domain.rankingContract.RankingEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingEmailEventTestPublisher {

    private final KafkaTemplate<String, RankingEmailEvent> kafkaTemplate;

    public void publishTop5EnterEvent() {
        RankingEmailEvent event = new RankingEmailEvent(
                "121235",
                RankingEventType.TOP5_ENTER,
                "테스트 유저 박다영",
                "forTestRanking@gmail.com",
                5,
                6,
                10);


        kafkaTemplate.send("RANKING_EMAIL_EVENT", event);

    }
}
