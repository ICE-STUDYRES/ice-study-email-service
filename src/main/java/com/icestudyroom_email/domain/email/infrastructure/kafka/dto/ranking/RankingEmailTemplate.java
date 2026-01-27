package com.icestudyroom_email.domain.email.infrastructure.kafka.dto.ranking;

import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
import com.icestudyroom_email.domain.rankingContract.RankingEventType;

public interface RankingEmailTemplate {
    RankingEventType supports();
    EmailRequest create(RankingEmailEvent event);
}
