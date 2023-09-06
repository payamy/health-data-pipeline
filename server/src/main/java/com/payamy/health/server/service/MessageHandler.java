package com.payamy.health.server.service;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface MessageHandler {
    void addSessionToPool( Long userId, WebSocketSession session );
    void sendMessageToUser(Long userId, String message) throws IOException;
    void removeSessionFromPool( Long userId, WebSocketSession session );
}
