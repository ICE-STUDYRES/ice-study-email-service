package com.icestudyroom_email.domain.infrastructure.socket.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class SocketIoStarter {

    private final SocketIOServer socketIOServer;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        socketIOServer.start();
    }
}
