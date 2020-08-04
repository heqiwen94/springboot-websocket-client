package com.hqw.websocket.websocket;

import com.hqw.websocket.ws.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@Slf4j
public class Client {

    public final static String WS_URL = "ws://127.0.0.1:8080/ws";
    public static void main(String[] args) {
        try {

            WebSocketClient webSocketClient = new StandardWebSocketClient();
            WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
            webSocketHttpHeaders.add(Constants.ORG_KEY, "123456");
            WebSocketSession webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    log.info("received message - " + message.getPayload());
                }

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("established connection - " + session);
                }

                @Override
                protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
                    log.info("handlePongMessage.......");
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                   log.info("afterConnectionClosed.......");
                }
            }, webSocketHttpHeaders, URI.create(WS_URL)).get();

            newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                try {
                    TextMessage message = new TextMessage("Hello !!");
                    webSocketSession.sendMessage(message);
                    log.info("sent message - " + message.getPayload());
                } catch (Exception e) {
                    log.error("Exception while sending a message", e);
                }
            }, 1, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception while accessing websockets", e);
        }
    }
}
