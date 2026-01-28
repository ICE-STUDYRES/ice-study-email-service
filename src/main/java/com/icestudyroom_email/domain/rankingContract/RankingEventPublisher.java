package com.icestudyroom_email.domain.rankingContract;

public abstract class RankingEventPublisher {

    public abstract void publish(RankingEmailEvent event);
}
