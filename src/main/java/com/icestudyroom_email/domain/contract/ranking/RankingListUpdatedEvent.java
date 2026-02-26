package com.icestudyroom_email.domain.contract.ranking;

import java.util.List;

public record RankingListUpdatedEvent(

        String eventId,
        String periodKey,
        List<WeeklyRankingDto> rankingList

) {}