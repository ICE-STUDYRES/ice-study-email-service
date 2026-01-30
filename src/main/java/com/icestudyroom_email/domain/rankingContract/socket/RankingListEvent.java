package com.icestudyroom_email.domain.rankingContract.socket;

import java.util.List;

public record RankingListEvent(
        RankingPeriod period,
        List<WeeklyRankingDto> rankings
) {}

