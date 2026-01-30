package com.icestudyroom_email.domain.email.infrastructure.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class DltConsumer {

    @KafkaListener(topics = "vacancy-notifications.DLT", groupId = "dlt-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeDlt(
        ConsumerRecord<String, String> record,
        @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage,
        @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE) String stacktrace,
        @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
        @Header(KafkaHeaders.DLT_ORIGINAL_OFFSET) long originalOffset
    ) {
        log.error("[DLT] 최종 처리 실패 메시지 수신");
        log.error("  - 토픽: {}", originalTopic);
        log.error("  - 오프셋: {}", originalOffset);
        log.error("  - 예외 메세지: {}", exceptionMessage);
        log.error("  - 예외 본문: {}", record.value());
        log.error("  - 스택 트레이스: {}", stacktrace);
    }
}
