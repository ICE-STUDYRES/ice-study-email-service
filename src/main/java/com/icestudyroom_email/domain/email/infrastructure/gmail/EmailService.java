package com.icestudyroom_email.domain.email.infrastructure.gmail;

import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest emailRequest);
}
