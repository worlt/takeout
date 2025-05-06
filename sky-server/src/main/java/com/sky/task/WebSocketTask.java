package com.sky.task;

import com.sky.WebSocketServer.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author worlt
 * @Date 2025/5/6 下午10:07
 */
@Component
public class WebSocketTask {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 通过WebSocket每个5秒向客户端发送消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        webSocketServer.sendToAllClient("这是来着服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }
}
