package com.icestudyroom_email.domain.common.kafka.producer;

import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
import com.icestudyroom_email.domain.rankingContract.forDelete.RankingEmailEventPublisher;
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
