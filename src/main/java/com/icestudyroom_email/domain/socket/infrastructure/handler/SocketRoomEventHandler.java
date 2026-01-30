package com.icestudyroom_email.domain.socket.infrastructure.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketRoomEventHandler {

    public void onJoin(SocketIOClient client, String room) {
        client.joinRoom(room);
        log.info("Client {} joined room {}", client.getSessionId(), room);
    }

    public void onLeave(SocketIOClient client, String room) {
        client.leaveRoom(room);
        log.info("Client {} left room {}", client.getSessionId(), room);
    }
}
