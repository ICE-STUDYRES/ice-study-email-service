package com.icestudyroom_email.domain.socket.infrastructure.ranking.dto;

public record WeeklyRankingItem(
        Long memberId,
        String memberName,
        int rank,
        int score
) {}


