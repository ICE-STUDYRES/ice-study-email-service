package com.icestudyroom_email.domain.contract.ranking;


public record RankingChangedEvent(

        String eventId,
        RankingEventType eventType,
        String periodKey,

        Long memberId,
        String name,
        String email,

        int currentRank,
        Integer previousRank,
        int score,
        Integer gapWithUpper

) {}