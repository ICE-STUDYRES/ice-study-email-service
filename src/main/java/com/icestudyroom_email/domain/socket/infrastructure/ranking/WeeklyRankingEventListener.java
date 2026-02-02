package com.icestudyroom_email.domain.socket.infrastructure.ranking;

import com.icestudyroom_email.domain.rankingContract.RankingListEvent;
import com.icestudyroom_email.domain.rankingContract.WeeklyRankingDto;
import com.icestudyroom_email.domain.socket.infrastructure.protocol.ranking.RankingSocketEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklyRankingEventListener {

    private final SocketRankingBroadcaster broadcaster;

    @EventListener
    public void handle(RankingListEvent event) {

        List<WeeklyRankingDto> rankings = event.rankings();

        broadcaster.broadcast(
                RankingSocketEvent.WEEKLY_RANKING_UPDATE,
                rankings
        );
    }
}