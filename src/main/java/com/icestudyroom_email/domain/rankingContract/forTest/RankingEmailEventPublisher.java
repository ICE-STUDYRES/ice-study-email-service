package com.icestudyroom_email.domain.rankingContract.forTest;

import com.icestudyroom_email.domain.contract.ranking.RankingEmailEvent;

public abstract class RankingEmailEventPublisher {

    public abstract void publish(RankingEmailEvent event);
}
