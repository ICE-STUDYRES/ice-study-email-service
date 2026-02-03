package com.icestudyroom_email.domain.common.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(
        name = "feature.kafka.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class KafkaRankingTopicConfig {

    @Bean
    public NewTopic rankingEmailTopic() {
        return TopicBuilder.name("RANKING_EMAIL_EVENT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
