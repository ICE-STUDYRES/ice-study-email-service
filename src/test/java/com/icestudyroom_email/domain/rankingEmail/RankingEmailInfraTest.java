package com.icestudyroom_email.domain.rankingEmail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RankingEmailInfraTest {

    @Autowired
    RankingEmailEventTestPublisher publisher;

    @Test
    void rankingFlowTest() {
        publisher.publishTop5EnterEvent();
    }
}
