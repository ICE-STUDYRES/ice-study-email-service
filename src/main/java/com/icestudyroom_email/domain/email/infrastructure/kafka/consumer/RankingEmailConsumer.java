package com.icestudyroom_email.domain.email.infrastructure.kafka.consumer;

import com.icestudyroom_email.domain.email.infrastructure.gmail.EmailService;
import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.email.infrastructure.idempotency.EmailIdempotencyService;
import com.icestudyroom_email.domain.email.infrastructure.template.RankingEmailTemplateResolver;
import com.icestudyroom_email.domain.rankingContract.email.RankingEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "feature.kafka.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class RankingEmailConsumer {

    private final EmailService emailService;
    private final RankingEmailTemplateResolver templateResolver;
    private final EmailIdempotencyService idempotencyService;

    @KafkaListener(
            topics = "RANKING_EMAIL_EVENT",
            groupId = "email-group",
            containerFactory = "rankingKafkaListenerContainerFactory"
    )
    public void consume(RankingEmailEvent event, Acknowledgment ack) {

        String checkDuplicatedkey = "email:sent:" + event.eventId();

        try {
            if (!idempotencyService.isFirst(checkDuplicatedkey, Duration.ofDays(7))) {
                log.info("Duplicate email ignored. eventId={}", event.eventId());
                ack.acknowledge();
                return;
            }

            log.info("[Kafka] 이메일 이벤트 수신 - eventType={}, email={}",
                    event.eventType(), event.email());

            EmailRequest emailRequest = templateResolver.resolve(event);
            log.info("[Kafka] 이메일 발송 요청 시작");
            emailService.sendEmail(emailRequest);
            log.info("[Kafka] 이메일 발송 요청 종료");

        } catch (Exception e) {
            log.error("이메일 발송 처리 중 예외 발생. eventId={}", event.eventId(), e);

        } finally {
            ack.acknowledge();
        }
    }
}
