package com.icestudyroom_email.domain.rankingContract.email;

public abstract class RankingEmailEventPublisher {

    public abstract void publish(RankingEmailEvent event);
}
