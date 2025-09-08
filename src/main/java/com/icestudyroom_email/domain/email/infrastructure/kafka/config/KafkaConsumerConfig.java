package com.icestudyroom_email.domain.email.infrastructure.kafka.config;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaConsumerConfig(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties(null);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.icestudyroom_email.domain.email.infrastructure.kafka.dto.VacancyNotificationRequest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {

        // 재시도 로직 비활성화 - 멱등성 키 방식 사용으로 인한 중복 방지 우선
       /*
       DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
               kafkaTemplate,
               (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1)
       );

       ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
       backOff.setMaxElapsedTime(10000L);
       DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
       */

        // 단순 로깅용 에러 핸들러 (재시도 없음)
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("[KAFKA_ERROR] 메시지 처리 중 예외 발생 - topic: {}, partition: {}, offset: {}, error: {}",
                    record.topic(), record.partition(), record.offset(), exception.getMessage());
        });

        errorHandler.addNotRetryableExceptions(
                NullPointerException.class,
                IllegalArgumentException.class,
                JsonParseException.class,
                HttpMessageNotReadableException.class
        );

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}