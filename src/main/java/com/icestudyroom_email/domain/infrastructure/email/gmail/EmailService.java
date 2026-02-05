package com.icestudyroom_email.domain.infrastructure.email.gmail;

public interface EmailService {
    void sendEmail(EmailRequest emailRequest);
}
