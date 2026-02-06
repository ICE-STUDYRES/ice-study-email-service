package com.icestudyroom_email.domain.infrastructure.email.gmail.Implement;

import com.icestudyroom_email.domain.infrastructure.email.gmail.EmailService;
import com.icestudyroom_email.domain.infrastructure.email.gmail.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Retryable(
            value = {
                    MailSendException.class,
                    RuntimeException.class
            },
            exclude = {
                    MailAuthenticationException.class,
                    IllegalArgumentException.class,
                    IllegalStateException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000L, multiplier = 2.0, maxDelay = 8000L)
    )
    public void sendEmail(EmailRequest emailRequest) {
        try {
            log.debug("이메일 발송 시도 - 수신자: {}, 제목: {}",
                    maskEmail(emailRequest.getTo()), emailRequest.getSubject());

            // 이메일 주소 유효성 체크
            validateEmailAddress(emailRequest.getTo());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), true);

            long startTime = System.currentTimeMillis();
            mailSender.send(mimeMessage);
            long endTime = System.currentTimeMillis();

            log.info("이메일 발송 성공 - 수신자: {}, 제목: {}, 소요시간: {}ms",
                    maskEmail(emailRequest.getTo()), emailRequest.getSubject(), (endTime - startTime));

        } catch (MailAuthenticationException e) {
            log.error("Gmail 인증 실패 - 재시도 안함: {}", e.getMessage());
            throw new IllegalStateException("Gmail 인증 설정을 확인해주세요.", e);

        } catch (IllegalArgumentException e) {
            log.error("잘못된 이메일 주소 - 재시도 안함: {}, 이메일: {}",
                    e.getMessage(), maskEmail(emailRequest.getTo()));
            throw e;

        } catch (MailSendException e) {
            log.warn("Gmail 발송 오류 - 재시도 예정: 수신자={}, 에러={}",
                    maskEmail(emailRequest.getTo()), e.getMessage());
            throw e;

        } catch (MessagingException e) {
            String message = e.getMessage();
            if (message != null) {
                if (message.contains("authentication") || message.contains("401")) {
                    log.error("Gmail 인증 관련 오류 - 재시도 안함: {}", message);
                    throw new IllegalStateException("Gmail 인증 설정을 확인해주세요.", e);
                }
                if (message.contains("invalid") || message.contains("address")) {
                    log.error("이메일 주소 관련 오류 - 재시도 안함: {}", message);
                    throw new IllegalArgumentException("유효하지 않은 이메일 주소입니다.", e);
                }
            }

            log.warn("메시징 오류 - 재시도 예정: 수신자={}, 에러={}",
                    maskEmail(emailRequest.getTo()), e.getMessage());
            // 체크 예외를 언체크 예외로 변환
            throw new RuntimeException("메시징 오류 발생", e);

        } catch (Exception e) {
            log.error("예상치 못한 이메일 발송 오류 - 수신자: {}, 에러: {}",
                    maskEmail(emailRequest.getTo()), e.getMessage(), e);
            throw new RuntimeException("메일 전송에 실패했습니다.", e);
        }
    }

    /**
     * 모든 재시도가 실패했을 때 호출되는 복구 메소드
     */
    @Recover
    public void recover(RuntimeException ex, EmailRequest emailRequest) {
        log.error("이메일 발송 최종 실패 - 모든 재시도 소진: 수신자={}, 제목={}, 최종오류={}",
                maskEmail(emailRequest.getTo()),
                emailRequest.getSubject(),
                ex.getMessage());

        Throwable cause = ex.getCause();
        if (cause instanceof MessagingException) {
            log.error("원본 MessagingException: {}", cause.getMessage());
        }
    }

    /**
     * 간단한 이메일 주소 유효성 검증
     */
    private void validateEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일 주소가 비어있습니다.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다: " + maskEmail(email));
        }
    }

    /**
     * 이메일 주소 마스킹 (개인정보 보호)
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "**@" + domain;
        }

        return username.substring(0, 2) + "***@" + domain;
    }
}