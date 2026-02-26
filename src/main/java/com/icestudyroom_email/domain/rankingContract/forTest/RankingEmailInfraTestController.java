package com.icestudyroom_email.domain.rankingContract.forTest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/infra-test/email")
@Profile("local")
public class RankingEmailInfraTestController {

    private final RankingEventTestPublisher userPublisher;
    private final RankingListUpdatedTestPublisher listPublisher;

    @PostMapping("/user/top5")
    public void triggerTop5() {
        userPublisher.publish();
    }

    @PostMapping("/list/update")
    public void triggerListUpdate() {
        listPublisher.publish();
    }
}
