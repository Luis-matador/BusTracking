package config;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws Exception {
        // Aquí procesaríamos mensajes enviados desde el cliente si fuera necesario
    }

    public void sendUpdates(String message) throws Exception {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new org.springframework.web.socket.TextMessage(message));
        }
    }
}