package com.icestudyroom_email.domain.socket.infrastructure.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.icestudyroom_email.domain.socket.infrastructure.handler.SocketRoomEventHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIoConfig {

    @Value("${socket.port}")
    private int port;

    @Value("${socket.origin}")
    private String origin;

    @Value("${socket.worker-threads}")
    private int workerThreads;

    @Value("${socket.boss-threads}")
    private int bossThreads;

    @Value("${socket.namespace}")
    private String namespace;

    @Bean(destroyMethod = "stop")
    public SocketIOServer socketIOServer(SocketRoomEventHandler handler) {

        Configuration config = new Configuration();
        config.setPort(port);
        config.setOrigin(origin);
        config.setAllowCustomRequests(true);
        config.setWorkerThreads(workerThreads);
        config.setBossThreads(bossThreads);

        SocketIOServer server = new SocketIOServer(config);

        // namespace 생성
        var rankingNamespace = server.addNamespace("/ranking");

        // join 이벤트 바인딩
        rankingNamespace.addEventListener(
                "join",
                String.class,
                (client, room, ackRequest) -> handler.onJoin(client, room)
        );

        rankingNamespace.addEventListener(
                "leave",
                String.class,
                (client, room, ackRequest) -> handler.onLeave(client, room)
        );

        return server;
    }
}
