package com.icestudyroom_email.domain.rankingContract.forDelete;

import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;

public abstract class RankingEmailEventPublisher {

    public abstract void publish(RankingEmailEvent event);
}
