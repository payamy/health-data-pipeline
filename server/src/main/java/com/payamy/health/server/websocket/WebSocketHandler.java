package com.payamy.health.server.websocket;

import com.payamy.health.server.entity.AccessToken;
import com.payamy.health.server.entity.Event;
import com.payamy.health.server.entity.User;
import com.payamy.health.server.repo.AccessTokenRepository;
import com.payamy.health.server.repo.CacheRepository;
import com.payamy.health.server.repo.UserRepository;
import com.payamy.health.server.service.MessageHandler;
import com.payamy.health.server.service.MessageSender;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Calendar;

public abstract class WebSocketHandler extends TextWebSocketHandler {

    protected String topic;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private MessageHandler messageHandler;

    private Long getSenderUserId(WebSocketSession session) {
        Long senderUserId = 0L;
        String[] parameters = session.getUri().getQuery().split("=");

        if (parameters.length == 2 && parameters[0].equals("accessToken")) {
            String accessToken = parameters[1];

            String senderId = cacheRepository.getUserIdByAccessToken(accessToken);

            if (senderId == null) {
                AccessToken accessTokenObj = accessTokenRepository.findByToken(accessToken);
                if (accessTokenObj != null) {
                    senderUserId = accessTokenObj.getUserId();
                }
            } else {
                senderUserId = Long.valueOf(senderId);
            }
        }
        return senderUserId;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long senderUserId = getSenderUserId(session);
        if (senderUserId == 0) {
            return;
        }

        messageHandler.removeSessionFromPool(senderUserId, session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            Long senderUserId = getSenderUserId(session);
            if (senderUserId == 0) {
                return;
            }
            messageHandler.addSessionToPool(senderUserId, session);
        }
        catch (Exception e) {
            session.close();
        }
    }
    @Autowired
    private MessageSender sender;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        Long senderUserId = getSenderUserId(session);
        if (senderUserId == 0) {
            return;
        }

        JSONObject jsonObject = new JSONObject(textMessage.getPayload());

        if (topic == null) {
            return;
        }

        JSONObject valueJson = jsonObject.getJSONObject("message").getJSONObject("value");
        System.out.println(valueJson);

//        User user = userRepository.findById(senderUserId).
//                orElse(User.builder()
//                        .build());
        Event event = Event.builder()
                .eventName(valueJson.getString("eventName"))
                .userId(senderUserId)
                .payload(valueJson.getJSONObject("payload").toString())
                .sentAt(Calendar.getInstance().getTime())
                .build();

        sender.sendMessage(topic, event);
    }
}
