package com.icestudyroom_email.domain.socket.infrastructure.ranking;

import com.corundumstudio.socketio.SocketIOServer;
import com.icestudyroom_email.domain.socket.infrastructure.protocol.SocketEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketRankingBroadcaster {

    private final SocketIOServer socketIOServer;
    private static final String NAMESPACE = "/ranking";

    public void broadcast(SocketEvent event, Object payload) {

        var namespace = socketIOServer.getNamespace(NAMESPACE);
        var roomOps = namespace.getRoomOperations(event.getRoom());

        log.info(
                "[SocketBroadcast] namespace={}, room={}, event={}, clients={}, payload={}",
                NAMESPACE,
                event.getRoom(),
                event.getEvent(),
                roomOps.getClients().size(),
                payload
        );

        roomOps.sendEvent(event.getEvent(), payload);
    }
}
