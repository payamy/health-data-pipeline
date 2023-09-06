package com.payamy.health.server.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JogWebSocketHandler extends WebSocketHandler {

    @Override
    protected void handleTextMessage( WebSocketSession session, TextMessage textMessage) throws Exception {
        topic = "movement";
        super.handleTextMessage(session, textMessage);
    }
}
