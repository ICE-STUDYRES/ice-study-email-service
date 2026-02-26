package com.icestudyroom_email.domain.rankingContract.forTest;

import com.icestudyroom_email.domain.contract.ranking.RankingListUpdatedEvent;
import com.icestudyroom_email.domain.contract.ranking.WeeklyRankingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile("local")
@RequiredArgsConstructor
public class RankingListUpdatedTestPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void publish() {

        RankingListUpdatedEvent event =
                new RankingListUpdatedEvent(
                        UUID.randomUUID().toString(),
                        "2026-W08",
                        List.of(
                                new WeeklyRankingDto(1,"박*영", 1500),
                                new WeeklyRankingDto(2,"김*준", 1400),
                                new WeeklyRankingDto(3,"김*희", 1400),
                                new WeeklyRankingDto(4,"변*빈", 1400),
                                new WeeklyRankingDto(5,"장*연", 1400)
                        )
                );

        kafkaTemplate.send("RANKING_LIST_UPDATED_EVENT", event);
    }
}
