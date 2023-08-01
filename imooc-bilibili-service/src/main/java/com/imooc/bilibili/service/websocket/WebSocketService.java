package com.imooc.bilibili.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.domain.DanMu;
import com.imooc.bilibili.service.DanMuService;
import com.imooc.bilibili.service.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述: WebSocket服务类
 */

@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    public static final ConcurrentMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    private Long userId;

    private static ApplicationContext APPLICATION_CONTEXT;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }



    @OnOpen
    public void openConnection(Session session, @PathParam("token") String token) {
        this.userId = TokenUtil.verifyToken(token);
        this.sessionId = session.getId();
        this.session = session;
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        } else {
            WEBSOCKET_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户: " + sessionId + "连接成功" + ",当前在线人数: " + ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("连接异常");
        }
    }

    @OnClose
    public void closeConnection(Session session) {
        String sessionId = session.getId();
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户: " + sessionId + "断开连接, 当前在线人数: " + ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        logger.info("用户信息: " + sessionId + ", 报文 : " + message);
        if (!StringUtil.isNullOrEmpty(message)) {

            //群发消息给所以连接了websocket的客户端
            try {
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    WebSocketService webSocketService = entry.getValue();
                    webSocketService.sendMessage(message);
                }
                DanMu danMu = JSONObject.parseObject(message, DanMu.class);
                if (userId != null) {
                    //保存弹幕到数据库
                    danMu.setUserId(userId);
                    danMu.setCreateTime(new Date());
                    DanMuService danMuService = (DanMuService) APPLICATION_CONTEXT.getBean("danMuService");
                    danMuService.addDanMu(danMu);
                    //保存弹幕到redis
                    danMuService.addDanMusToRedis(danMu);
                }
            } catch (Exception e) {
                logger.error("弹幕接收错误");
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable error) {

    }

    public void sendMessage(String msg) throws IOException {
        session.getBasicRemote().sendText(msg);
    }
}
