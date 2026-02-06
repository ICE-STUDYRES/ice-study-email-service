package com.icestudyroom_email.domain.infrastructure.socket.protocol.ranking;

import com.icestudyroom_email.domain.infrastructure.socket.protocol.SocketEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankingSocketEvent implements SocketEvent {

    WEEKLY_RANKING_UPDATE("weekly", "weekly-ranking-update");

    private final String room;
    private final String event;

}
