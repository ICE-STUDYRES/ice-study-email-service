package com.icestudyroom_email.domain.contract.ranking;

public record WeeklyRankingDto(
        int rank,
        String name,
        int score
) {}
