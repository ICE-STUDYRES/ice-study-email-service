package com.icestudyroom_email.domain.application.socket;

import com.icestudyroom_email.domain.contract.ranking.RankingListEvent;
import com.icestudyroom_email.domain.contract.ranking.WeeklyRankingDto;
import com.icestudyroom_email.domain.infrastructure.socket.protocol.ranking.RankingSocketEvent;
import com.icestudyroom_email.domain.infrastructure.socket.broadcaster.SocketRankingBroadcaster;
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