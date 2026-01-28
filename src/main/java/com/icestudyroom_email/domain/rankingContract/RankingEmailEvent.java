package com.icestudyroom_email.domain.rankingContract;

public record RankingEmailEvent(

        String eventId,
        RankingEventType eventType,
        String name,
        String email,
        int currentRank,
        Integer previousRank,
        Integer gapWithUpper
) {}
