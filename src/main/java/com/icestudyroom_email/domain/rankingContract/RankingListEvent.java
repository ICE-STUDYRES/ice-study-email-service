package com.icestudyroom_email.domain.rankingContract;

import java.util.List;

public record RankingListEvent(
        List<WeeklyRankingDto> rankings
) {}

