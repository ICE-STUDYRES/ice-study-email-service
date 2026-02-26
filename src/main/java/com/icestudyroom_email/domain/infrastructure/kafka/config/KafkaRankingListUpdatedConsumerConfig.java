package com.icestudyroom_email.domain.infrastructure.kafka.config;

import com.icestudyroom_email.domain.contract.ranking.RankingListUpdatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@ConditionalOnProperty(
        name = "feature.kafka.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class KafkaRankingListUpdatedConsumerConfig {

    @Bean
    public ConsumerFactory<String, RankingListUpdatedEvent> rankingListConsumerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, RankingListUpdatedEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RankingListUpdatedEvent>
    rankingListKafkaListenerContainerFactory(
            ConsumerFactory<String, RankingListUpdatedEvent> factory) {

        ConcurrentKafkaListenerContainerFactory<String, RankingListUpdatedEvent> containerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();

        containerFactory.setConsumerFactory(factory);
        containerFactory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);

        return containerFactory;
    }
}
