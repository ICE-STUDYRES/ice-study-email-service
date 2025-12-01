package com.icestudyroom_email.domain.email.infrastructure.kafka.consumer;

import com.icestudyroom_email.domain.email.infrastructure.gmail.EmailService;
import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.email.infrastructure.kafka.dto.VacancyNotificationRequest;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventConsumer {
    private final static String reservationLink = "https://ice-studyroom.com";
    private final EmailService emailService;
    private final Retry emailRetry;

    private final Map<String, Long> processedMessageIds = new ConcurrentHashMap<>();
    private static final int MAX_PROCESSED_IDS = 50000;

    private static final Duration CLEANUP_AFTER = Duration.ofHours(6);

    private final AtomicInteger messageCounter = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger duplicateCount = new AtomicInteger(0);
    private volatile long lastProcessTime = System.currentTimeMillis();

    @KafkaListener(topics = "vacancy-notifications", groupId = "email-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenVacancyNotification(VacancyNotificationRequest notificationRequest, Acknowledgment ack) {
        lastProcessTime = System.currentTimeMillis();

        log.info("[KAFKA] 빈자리 알림 수신 - 스케줄:{}, 방:{}, 이메일:{}",
                notificationRequest.getScheduleId(),
                notificationRequest.getRoomName(),
                maskEmail(notificationRequest.getEmail()));

        String idempotencyKey = generateIdempotencyKey(notificationRequest);

        // 중복 메시지 체크
        if (processedMessageIds.containsKey(idempotencyKey)) {
            duplicateCount.incrementAndGet();
            log.warn("[DUPLICATE] 중복 메시지 차단 - 키:{}", idempotencyKey);
            ack.acknowledge();
            return;
        }

        // 멱등성 키 저장 (현재 시간과 함께)
        processedMessageIds.put(idempotencyKey, System.currentTimeMillis());

        try {
            long startTime = System.currentTimeMillis();

            Retry.decorateRunnable(emailRetry, () -> {
                String subject = createEmailSubject(notificationRequest);
                String emailBody = createEmailBody(notificationRequest);
                EmailRequest emailRequest = new EmailRequest(
                        notificationRequest.getEmail(),
                        subject,
                        emailBody
                );
                emailService.sendEmail(emailRequest);
            }).run();

            long endTime = System.currentTimeMillis();
            successCount.incrementAndGet();

            log.info("[SUCCESS] 이메일 발송 성공 - 스케줄:{}, 수신자:{}, 소요시간:{}ms",
                    notificationRequest.getScheduleId(),
                    maskEmail(notificationRequest.getEmail()),
                    (endTime - startTime));

        } catch (Exception e) {
            failureCount.incrementAndGet();
            log.error("[FAILURE] 이메일 발송 실패 - 스케줄:{}, 수신자:{}, 에러:{}",
                    notificationRequest.getScheduleId(),
                    maskEmail(notificationRequest.getEmail()),
                    e.getMessage());

            processedMessageIds.remove(idempotencyKey);
        } finally {
            ack.acknowledge();
            int currentCount = messageCounter.incrementAndGet();

            // 100개마다 모니터링
            if (currentCount % 100 == 0) logMonitoringStats();

            // 500개마다 초간단 정리
            if (currentCount % 500 == 0) cleanupOldKeys();
        }
    }

    /**
     * 저장된지 6시간 지난 키만 삭제
     */
    private void cleanupOldKeys() {
        long currentTime = System.currentTimeMillis();
        long expireTime = currentTime - CLEANUP_AFTER.toMillis();

        int removedCount = 0;
        Iterator<Map.Entry<String, Long>> iterator = processedMessageIds.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();

            // 저장 시간이 6시간 이전이면 삭제
            if (entry.getValue() < expireTime) {
                iterator.remove();
                removedCount++;
            }
        }

        // 응급 상황: 메모리 사용량이 너무 많으면 강제 정리
        if (processedMessageIds.size() > MAX_PROCESSED_IDS) {
            int emergencyRemoveCount = processedMessageIds.size() - (MAX_PROCESSED_IDS / 2);

            processedMessageIds.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue()) // 오래된 것부터
                    .limit(emergencyRemoveCount)
                    .map(Map.Entry::getKey)
                    .forEach(processedMessageIds::remove);

            removedCount += emergencyRemoveCount;
            log.warn("[EMERGENCY_CLEANUP] 메모리 한계로 강제 정리: {}개", emergencyRemoveCount);
        }

        if (removedCount > 0) {
            log.info("[CLEANUP] 6시간 경과 키 정리 완료 - 제거: {}개, 남은 키: {}개",
                    removedCount, processedMessageIds.size());
        }
    }

    private String generateIdempotencyKey(VacancyNotificationRequest request) {
        return String.format("schedule-%d-email-%s-time-%d",
                request.getScheduleId(),
                request.getEmail(),
                request.getTimestamp());
    }

    private String createEmailSubject(VacancyNotificationRequest dto) {
        return String.format("[ICE-STUDYRES] %s %s-%s 빈자리 알림",
                dto.getRoomName(),
                dto.getStartTime(),
                dto.getEndTime());
    }

    private String createEmailBody(VacancyNotificationRequest dto) {
        return String.format(
                "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                        "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h2 style='color: #2c5aa0; text-align: center; margin-bottom: 30px;'>🏫 빈자리 알림</h2>" +
                        "<hr style='border: none; height: 2px; background: linear-gradient(to right, #2c5aa0, #87ceeb); margin: 20px 0;'>" +

                        "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 6px; margin: 20px 0;'>" +
                        "<h3 style='color: #2c5aa0; margin-top: 0;'>📍 예약 가능한 빈자리 정보</h3>" +
                        "<p style='font-size: 16px; margin: 10px 0;'><strong>📅 날짜:</strong> %s</p>" +
                        "<p style='font-size: 16px; margin: 10px 0;'><strong>🏠 방 번호:</strong> %s</p>" +
                        "<p style='font-size: 18px; margin: 15px 0; color: #e74c3c;'><strong>⏰ 시간:</strong> %s ~ %s</p>" +
                        "</div>" +

                        "<div style='text-align: center; margin: 30px 0;'>" +
                        "<p style='font-size: 16px; margin-bottom: 20px;'>아래 링크를 클릭하여 <strong>빠르게 예약</strong>하세요!</p>" +
                        "<a href='%s' style='display: inline-block; background-color: #2c5aa0; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px;'>🖱️ 지금 예약하기</a>" +
                        "</div>" +

                        "<hr style='border: none; height: 1px; background-color: #eee; margin: 30px 0;'>" +
                        "<p style='text-align: center; color: #666; font-size: 14px;'>빈자리는 선착순이니 서둘러 예약하세요! 🏃‍♂️💨</p>" +
                        "<p style='text-align: center; color: #999; font-size: 12px; margin-top: 20px;'>ICE 스터디룸 예약 시스템</p>" +
                        "</div>" +
                        "</body></html>",
                dto.getEventDate(),
                dto.getRoomName(),
                dto.getStartTime(),
                dto.getEndTime(),
                reservationLink
        );
    }

    private void logMonitoringStats() {
        int total = successCount.get() + failureCount.get() + duplicateCount.get();
        double successRate = total > 0 ? (double) successCount.get() / total * 100 : 0;
        double duplicateRate = total > 0 ? (double) duplicateCount.get() / total * 100 : 0;
        long minutesSinceLastProcess = (System.currentTimeMillis() - lastProcessTime) / 60000;

        log.info("[MONITOR] === 초간단 6시간 정리 이메일 시스템 ===");
        log.info("[MONITOR] 총 처리: {}건 | 성공: {}건 | 실패: {}건 | 중복: {}건",
                total, successCount.get(), failureCount.get(), duplicateCount.get());
        log.info("[MONITOR] 성공률: {:.2f}% | 중복률: {:.2f}% | 멱등성 키: {}개",
                successRate, duplicateRate, processedMessageIds.size());
        log.info("[MONITOR] 마지막 처리: {}분 전 | 정리 기준: {}시간 후",
                minutesSinceLastProcess, CLEANUP_AFTER.toHours());

        // 간단한 알림
        if (duplicateRate > 1.0 && total > 50) {
            log.warn("[ALERT] 중복률 주의: {:.2f}%", duplicateRate);
        }
        if (processedMessageIds.size() > MAX_PROCESSED_IDS * 0.8) {
            log.warn("[ALERT] 메모리 사용량 높음: {}개", processedMessageIds.size());
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "**@" + domain;
        }
        return username.substring(0, 2) + "***@" + domain;
    }
}