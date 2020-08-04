package com.hqw.websocket.ws;

import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StompClient {

    private GatherJobHandleImpl handle;

//    public final static String WS_URL = "ws://127.0.0.1:10081/ws";

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    private StompSession stompSession;

    private String orgId;

    private String wsURL;

    public StompClient() {
    }

    public StompClient(String wsURL,String orgId, GatherJobHandleImpl handle) {
        this.handle = handle;
        this.orgId = orgId;
        this.wsURL = wsURL;
    }

    private ListenableFuture<StompSession> connect() {
        headers.set(Constants.ORG_KEY, orgId);

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setTaskScheduler(new DefaultManagedTaskScheduler());
        return stompClient.connect(wsURL, headers, new MyHandler());
    }

    public void doSubscribe() {
        stompSession.subscribe("/topic/greetings", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                log.info("orgId:{},Topic Received:{} ", orgId, new String((byte[]) o));
            }
        });
        stompSession.subscribe("/user/queue/message", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object object) {
                log.info("orgId:{},Queue Received:{}  ", orgId, new String((byte[]) object));
                String message = new String((byte[]) object);
                GatherControlVo controlVo = Json.fromJson(GatherControlVo.class, message);
//                GatherControlVo controlVo = JSONObject.parseObject(object.toString(), GatherControlVo.class);

                if (controlVo.getSyncState() == 0 && controlVo.getEnableState() == 1) {
                    try {
                        //  接受消息：更新采集中心的状态
                        handle.handle(controlVo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("前置机接收采集中心推送的命令执行失败,报错：" + e);
                    }
                }
            }
        });
    }

    public void sendHello() {
        String jsonHello = "{ \"name\" : \"Nick\" }";
        String gwHello = "{ \"name\" : \"GW\" }";
        stompSession.send("/ws2/hello", jsonHello.getBytes());
        stompSession.send("/gw/hello", gwHello.getBytes());
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            log.info("orgId:{} Now connected", orgId);

        }
    }

    public void start() {
        initSession();
        onlineCheck();
    }

    private void initSession() {
        ListenableFuture<StompSession> future = connect();
        try {
            log.info("connect.....");
            stompSession = future.get();
            //跟踪发送消息返回状态，必须启用该项
            stompSession.setAutoReceipt(true);
            doSubscribe();
        } catch (Exception e) {
            log.error("connect error:" + e.getMessage(), e);
        }
    }

    private void onlineCheck() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                if (stompSession == null || !stompSession.isConnected()) {
                    initSession();
                }
            } catch (Exception e) {
                log.error("Exception while sending a message", e);
            }
        }, 1, 10, TimeUnit.SECONDS);
    }
}
