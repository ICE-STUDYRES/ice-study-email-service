package com.icestudyroom_email.domain.email.infrastructure.template;

import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingEmailTemplateResolver {

    private final List<RankingEmailTemplate> templates;

    public EmailRequest resolve(RankingEmailEvent event) {
        return templates.stream()
                .filter(t -> t.supports() == event.eventType())
                .findFirst()
                .orElseThrow( () ->
                        new IllegalArgumentException("지원하지 않는 이벤트 타입:" + event.eventType()
                        )
                ).create(event);
    }

}
