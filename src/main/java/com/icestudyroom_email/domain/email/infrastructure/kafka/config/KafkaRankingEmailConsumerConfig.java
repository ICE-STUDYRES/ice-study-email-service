package com.icestudyroom_email.domain.email.infrastructure.kafka.config;

import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
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
public class KafkaRankingEmailConsumerConfig {

    @Bean
    public ConsumerFactory<String, RankingEmailEvent> rankingConsumerFactory
            (KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, RankingEmailEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

            return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RankingEmailEvent> rankingKafkaListenerContainerFactory(
            ConsumerFactory<String, RankingEmailEvent> rankingConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, RankingEmailEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(rankingConsumerFactory);
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
