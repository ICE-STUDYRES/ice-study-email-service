package com.icestudyroom_email.domain.email.infrastructure.kafka;

import com.icestudyroom_email.domain.email.infrastructure.gmail.EmailService;
import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.email.infrastructure.kafka.dto.VacancyNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventConsumer {
    private final static String reservationLink = "http://localhost:5173/reservation/room";
    private final EmailService emailService;

    @KafkaListener(topics = "vacancy-notifications", groupId = "email-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenVacancyNotification(VacancyNotificationRequest notificationRequest) {
        log.info("빈자리 알림 서비스 수행, 방 번호: {}", notificationRequest.getRoomName());

        String emailBody = createEmailBody(notificationRequest);
        EmailRequest emailRequest = new EmailRequest(notificationRequest.getEmail(), "[ICE-STUDYRES] 빈자리 알림", emailBody);

        emailService.sendEmail(emailRequest);
    }

    private String createEmailBody(VacancyNotificationRequest dto) {
        return String.format(
                "<html><body>" +
                        "<h2>빈자리 알림</h2>" +
                        "<hr>" +
                        "<p><strong>%s %s</strong>에 빈자리가 생겼습니다.</p>" +
                        "<p>아래 링크에 접속하시면 빠르게 예약하실 수 있습니다.</p>" +
                        "<p><strong>링크:</strong> <a href=\"%s\">%s</a></p>" +
                        "<hr>" +
                        "<p>감사합니다.</p>" +
                        "</body></html>",
                dto.getEventDate(),
                dto.getRoomName(),
                reservationLink,
                reservationLink
        );
    }
}
