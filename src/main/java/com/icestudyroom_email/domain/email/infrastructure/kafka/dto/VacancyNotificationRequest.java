package com.icestudyroom_email.domain.email.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VacancyNotificationRequest {
    private String email;
    private String eventDate;
    private String roomName;
}

