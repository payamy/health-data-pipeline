package com.payamy.health.server.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public HeartbeatWebSocketHandler heartbeatMessageHandler() {
        return new HeartbeatWebSocketHandler();
    }

    @Bean
    public JogWebSocketHandler jogMessageHandler() { return new JogWebSocketHandler(); }

    @Override
    public void registerWebSocketHandlers( WebSocketHandlerRegistry registry ) {
        registry.addHandler(heartbeatMessageHandler(), "/event/heartbeat").setAllowedOrigins("*");
        registry.addHandler(jogMessageHandler(), "/event/jog").setAllowedOrigins("*");
    }
}
