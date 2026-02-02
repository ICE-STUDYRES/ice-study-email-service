package com.icestudyroom_email.domain.socket.infrastructure.ranking;

import com.corundumstudio.socketio.SocketIOServer;
import com.icestudyroom_email.domain.rankingContract.PersonalNotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonalNotificationBroadcaster {

    private final SocketIOServer  socketIOServer;

    private static final String NAMESPACE = "/ranking";
    private static final String EVENT_NAME = "personal-notification";

    public void send(Long memberId, String type) {

        String room = "member:" + memberId;

        var namespace = socketIOServer.getNamespace(NAMESPACE);
        var roomOps = namespace.getRoomOperations(room);

        log.info(
                "[SocketNotify] send personal notification. room={}, type={}, clients={}",
                room,
                type,
                roomOps.getClients().size()
        );

        roomOps.sendEvent(
                EVENT_NAME,
                new PersonalNotificationPayload(type)
        );
    }
}
