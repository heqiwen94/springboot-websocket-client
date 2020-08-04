package com.hqw.websocket.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * websocket连接,系统启动的时候
 */
@Component
@Slf4j
public class AfterApplicationStart implements ApplicationRunner {

    @Value("${front.orgCode}")
    private String orgCode;

    @Value("${front.wsURL}")
    private String wsURL;

    @Resource
    private GatherJobHandleImpl handle;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        try {
            StompClient stompClient = new StompClient(wsURL,orgCode,handle);
            stompClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("websocket连接失败");
        }
    }
}
