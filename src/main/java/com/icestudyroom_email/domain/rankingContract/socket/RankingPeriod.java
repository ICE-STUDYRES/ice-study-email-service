package com.icestudyroom_email.domain.rankingContract.socket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankingPeriod {

    WEEKLY("ranking:weekly", 5),
    MONTHLY("ranking:monthly", 10), // 확장성 고려
    DAILY("ranking:daily", 10); // 확장성 고려

    private final String redisKey;
    private final int limit;
}
