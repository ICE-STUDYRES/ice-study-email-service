package com.icestudyroom_email.domain.email.infrastructure.kafka.producer;

import com.icestudyroom_email.domain.rankingContract.email.RankingEmailEvent;
import com.icestudyroom_email.domain.rankingContract.email.RankingEmailEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaRankingEventPublisher extends RankingEmailEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(RankingEmailEvent event) {
        kafkaTemplate.send("RANKING_EMAIL_EVENT", event);
    }
}
