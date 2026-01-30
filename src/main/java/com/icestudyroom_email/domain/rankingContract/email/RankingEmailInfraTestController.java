package com.icestudyroom_email.domain.rankingContract.email;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/infra-test/email")
public class RankingEmailInfraTestController {

    private final RankingEmailEventTestPublisher publisher;

    @PostMapping("/top5")
    public void triggerTop5() {
        publisher.publishTop5EnterEvent();
    }
}
