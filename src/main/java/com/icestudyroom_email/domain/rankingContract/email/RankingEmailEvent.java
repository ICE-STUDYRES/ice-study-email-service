package com.icestudyroom_email.domain.rankingContract.email;

import com.icestudyroom_email.domain.rankingContract.RankingEventType;

public record RankingEmailEvent(

        String eventId,
        RankingEventType eventType,
        String name,
        String email,
        int currentRank,
        Integer previousRank,
        Integer gapWithUpper
) {}
