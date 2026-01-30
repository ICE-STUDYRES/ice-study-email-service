package com.icestudyroom_email.domain.socket.infrastructure.ranking;

import com.icestudyroom_email.domain.rankingContract.socket.RankingListEvent;
import com.icestudyroom_email.domain.rankingContract.socket.RankingPeriod;
import com.icestudyroom_email.domain.rankingContract.socket.WeeklyRankingDto;
import com.icestudyroom_email.domain.socket.infrastructure.protocol.ranking.RankingSocketEvent;
import com.icestudyroom_email.domain.socket.infrastructure.ranking.dto.WeeklyRankingItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
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