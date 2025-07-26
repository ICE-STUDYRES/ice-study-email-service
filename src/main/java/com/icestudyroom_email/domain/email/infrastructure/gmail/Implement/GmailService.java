package com.icestudyroom_email.domain.email.infrastructure.gmail.Implement;


import com.icestudyroom_email.domain.email.infrastructure.gmail.EmailService;
import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailService implements EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailRequest emailRequest) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), true); // HTML 본문

            mailSender.send(mimeMessage);
            log.info("이메일 전송 성공 - 수신자: {}, 제목: {}", emailRequest.getTo(), emailRequest.getSubject());
        } catch (MessagingException e) {
            log.error("이메일 전송 실패 - 수신자: {}, 제목: {}", emailRequest.getTo(), emailRequest.getSubject(), e);
            throw new RuntimeException("메일 전송에 실패했습니다.", e);
        }
    }
}
