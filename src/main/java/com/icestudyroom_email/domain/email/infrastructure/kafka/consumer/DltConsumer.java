package com.icestudyroom_email.domain.email.infrastructure.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
        log.error("  - Original Topic: {}", originalTopic);
        log.error("  - Original Offset: {}", originalOffset);
        log.error("  - Exception: {}", exceptionMessage);
        log.error("  - Failed Message Payload: {}", record.value());
        log.error("  - Stacktrace: {}", stacktrace);
    }
}
