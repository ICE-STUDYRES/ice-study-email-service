package com.icestudyroom_email.domain.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class EmailSystemHealthChecker {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 10분마다 시스템 상태 체크
    @Scheduled(fixedRate = 600000)
    public void healthCheck() {
        try {
            // JVM 메모리 체크
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
            long totalMemory = runtime.totalMemory() / 1024 / 1024;
            long freeMemory = runtime.freeMemory() / 1024 / 1024;
            long usedMemory = totalMemory - freeMemory;
            double memoryUsage = (double) usedMemory / maxMemory * 100;

            System.gc();
            long afterGcMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

            log.info("[HEALTH] === 시스템 헬스체크 ===");
            log.info("[HEALTH] JVM 메모리: {}MB / {}MB ({:.1f}%)", usedMemory, maxMemory, memoryUsage);
            log.info("[HEALTH] GC 후 메모리: {}MB", afterGcMemory);
            log.info("[HEALTH] 애플리케이션 상태: RUNNING");
            log.info("[HEALTH] 타임스탬프: {}", LocalDateTime.now().format(FORMATTER));

            checkMemoryAlerts(memoryUsage, afterGcMemory, maxMemory);

        } catch (Exception e) {
            log.error("[HEALTH] 헬스체크 실패: {}", e.getMessage());
        }
    }

    private void checkMemoryAlerts(double memoryUsage, long afterGcMemory, long maxMemory) {
        // 메모리 사용량 알림
        if (memoryUsage > 90) {
            log.error("[ALERT] JVM 메모리 사용량 위험! {:.1f}% - 즉시 확인 필요", memoryUsage);
        } else if (memoryUsage > 80) {
            log.warn("[ALERT] JVM 메모리 사용량 높음! {:.1f}%", memoryUsage);
        }

        // GC 후에도 메모리가 높으면 실제 사용량이 많은 것
        double gcMemoryUsage = (double) afterGcMemory / maxMemory * 100;
        if (gcMemoryUsage > 70) {
            log.warn("[ALERT] GC 후에도 메모리 사용량 높음! {:.1f}% - 메모리 누수 의심", gcMemoryUsage);
        }
    }
}