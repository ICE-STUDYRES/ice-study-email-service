package com.icestudyroom_email.domain.email.infrastructure.kafka.consumer;

import com.icestudyroom_email.domain.email.infrastructure.gmail.EmailService;
import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.email.infrastructure.kafka.dto.VacancyNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventConsumer {
    private final static String reservationLink = "https://ice-studyroom.com";
    private final EmailService emailService;

    private final Set<String> processedMessageIds = ConcurrentHashMap.newKeySet();
    private static final int MAX_PROCESSED_IDS = 50000;
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

        if (!processedMessageIds.add(idempotencyKey)) {
            duplicateCount.incrementAndGet();
            log.warn("[DUPLICATE] 중복 메시지 차단 - 키:{}", idempotencyKey);
            logMonitoringStats();
            ack.acknowledge();
            return;
        }

        try {
            String subject = createEmailSubject(notificationRequest);
            String emailBody = createEmailBody(notificationRequest);
            EmailRequest emailRequest = new EmailRequest(notificationRequest.getEmail(), subject, emailBody);

            long startTime = System.currentTimeMillis();
            emailService.sendEmail(emailRequest);
            long endTime = System.currentTimeMillis();

            successCount.incrementAndGet();
            ack.acknowledge();

            log.info("[SUCCESS] 이메일 발송 성공 - 스케줄:{}, 수신자:{}, 소요시간:{}ms",
                    notificationRequest.getScheduleId(),
                    maskEmail(notificationRequest.getEmail()),
                    (endTime - startTime));

        } catch (Exception e) {
            failureCount.incrementAndGet();
            processedMessageIds.remove(idempotencyKey);

            log.error("[FAILURE] 이메일 발송 실패 - 스케줄:{}, 수신자:{}, 에러:{}",
                    notificationRequest.getScheduleId(),
                    maskEmail(notificationRequest.getEmail()),
                    e.getMessage());

            throw e;
        }

        int currentCount = messageCounter.incrementAndGet();

        // 100개마다 모니터링 로그 출력
        if (currentCount % 100 == 0) {
            logMonitoringStats();
        }

        // 1000개마다 cleanup 체크
        if (currentCount % 1000 == 0) {
            cleanupIfNeeded();
        }
    }

    private void cleanupIfNeeded() {
        if (processedMessageIds.size() > MAX_PROCESSED_IDS) {
            // 절반 정리 (오래된 것부터)
            int removeCount = processedMessageIds.size() / 2;
            Iterator<String> iterator = processedMessageIds.iterator();

            for (int i = 0; i < removeCount && iterator.hasNext(); i++) {
                iterator.next();
                iterator.remove();
            }

            log.info("메모리 정리 완료 - 제거된 항목: {}, 남은 항목: {}",
                    removeCount, processedMessageIds.size());
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
        long minutesSinceLastProcess = (System.currentTimeMillis() - lastProcessTime) / 60000;

        log.info("[MONITOR] === 이메일 시스템 통계 ===");
        log.info("[MONITOR] 총 처리: {}건 | 성공: {}건 | 실패: {}건 | 중복: {}건",
                total, successCount.get(), failureCount.get(), duplicateCount.get());
        log.info("[MONITOR] 성공률: {:.2f}% | 메모리 사용: {}개 키 | 마지막 처리: {}분 전",
                successRate, processedMessageIds.size(), minutesSinceLastProcess);

        // 알림 레벨 판정
        if (successRate < 95.0 && total > 10) {
            log.error("[ALERT] 성공률 낮음! 현재: {:.2f}%", successRate);
        }
        if (processedMessageIds.size() > MAX_PROCESSED_IDS * 0.8) {
            log.warn("[ALERT] 메모리 사용량 높음! 현재: {}개", processedMessageIds.size());
        }
        if (minutesSinceLastProcess > 30) {
            log.warn("[ALERT] 오랫동안 메시지 없음! {}분 전", minutesSinceLastProcess);
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
