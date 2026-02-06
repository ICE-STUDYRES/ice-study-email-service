package com.icestudyroom_email.domain.infrastructure.email.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.core.IntervalFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;

import jakarta.mail.MessagingException;
import java.time.Duration;

@Slf4j
@Configuration
public class EmailRetryConfig {

    @Bean
    public Retry emailRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                        Duration.ofSeconds(2),
                        2.0
                ))
                .retryExceptions(
                        MailSendException.class,
                        MailException.class,
                        MessagingException.class
                )
                .ignoreExceptions(
                        IllegalArgumentException.class,
                        NullPointerException.class
                )
                .build();

        Retry retry = Retry.of("email-send", config);

        retry.getEventPublisher()
                .onRetry(event ->
                        log.warn("[RETRY] 이메일 재시도 - 시도 #{}, 예외: {}",
                                event.getNumberOfRetryAttempts(),
                                event.getLastThrowable().getClass().getSimpleName()))
                .onSuccess(event -> {
                    if (event.getNumberOfRetryAttempts() > 0) {
                        log.info("[RETRY_SUCCESS] {} 번 만에 이메일 발송 성공",
                                event.getNumberOfRetryAttempts() + 1);
                    }
                })
                .onError(event ->
                        log.error("[RETRY_FAILED] 모든 재시도 실패 - 총 {} 회 시도",
                                event.getNumberOfRetryAttempts()));

        return retry;
    }
}