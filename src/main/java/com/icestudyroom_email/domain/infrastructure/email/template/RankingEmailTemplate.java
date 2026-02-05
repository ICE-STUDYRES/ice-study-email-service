package com.icestudyroom_email.domain.infrastructure.email.template;

import com.icestudyroom_email.domain.infrastructure.email.gmail.EmailRequest;
import com.icestudyroom_email.domain.contract.ranking.RankingEmailEvent;
import com.icestudyroom_email.domain.contract.ranking.RankingEventType;

public interface RankingEmailTemplate {
    RankingEventType supports();
    EmailRequest create(RankingEmailEvent event);
}
