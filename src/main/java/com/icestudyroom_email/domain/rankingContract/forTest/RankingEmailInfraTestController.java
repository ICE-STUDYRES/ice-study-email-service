package com.icestudyroom_email.domain.rankingContract.forTest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/infra-test/email")
public class RankingEmailInfraTestController {

    private final RankingEventTestPublisher publisher;

    @PostMapping("/top5")
    public void triggerTop5() {
        publisher.publishTop5EnterEvent();
    }

    @PostMapping("/personal/{memberId}")
    public void triggerPersonalNotification(
            @PathVariable Long memberId
    ) {
        publisher.publishPersonalNotificationEvent(memberId);
    }
}
