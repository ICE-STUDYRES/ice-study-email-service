package com.icestudyroom_email.domain.contract.ranking;

import java.util.List;

public record RankingListEvent(
        List<WeeklyRankingDto> rankings
) {}

