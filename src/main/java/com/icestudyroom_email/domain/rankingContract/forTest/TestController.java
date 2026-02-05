package com.icestudyroom_email.domain.rankingContract.forTest;

import com.icestudyroom_email.domain.contract.ranking.WeeklyRankingDto;
import com.icestudyroom_email.domain.infrastructure.socket.protocol.ranking.RankingSocketEvent;
import com.icestudyroom_email.domain.infrastructure.socket.broadcaster.SocketRankingBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final SocketRankingBroadcaster broadcaster;

    @PostMapping("/test/ranking/weekly")
    public ResponseEntity<String> testWeeklyRanking() {

        broadcaster.broadcast(
                RankingSocketEvent.WEEKLY_RANKING_UPDATE,
                mockWeeklyRanking()
        );

        return ResponseEntity.ok("weekly ranking broadcast triggered");
    }

    private List<WeeklyRankingDto> mockWeeklyRanking() {
        return List.of(
                new WeeklyRankingDto(1, 1001L, "박*영", 150),
                new WeeklyRankingDto(2, 1002L, "김*준", 120),
                new WeeklyRankingDto(3, 1003L, "임*연", 95),
                new WeeklyRankingDto(4, 1004L, "김*희", 80),
                new WeeklyRankingDto(5, 1005L, "장*연", 70)
        );
    }
}

