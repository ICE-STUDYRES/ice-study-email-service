package com.icestudyroom_email.domain.contract.ranking;

public record RankingEmailEvent(

        String eventId,
        RankingEventType eventType,
        Long memberId,
        String name,
        String email,
        int currentRank,
        Integer previousRank,
        Integer gapWithUpper
) {}
