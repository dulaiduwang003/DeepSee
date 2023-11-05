package com.cn.net;

import com.alibaba.fastjson.JSONObject;
import com.cn.enums.EnvEnum;
import com.cn.model.ChatGptModel;
import com.cn.service.impl.ChatGptServiceFlux;
import com.cn.utils.ChatGptUtil;
import com.cn.utils.SpringContextUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * The type Chat net.
 */
@Slf4j
@ServerEndpoint(value = "/chat/{index}/{env}/{token}", subprotocols = {"protocol"})
@SuppressWarnings("all")
@Service
public class ChatGptNet {

    private Session session;

    private static ChatGptServiceFlux chatGptServiceFlux;

    private static ChatGptUtil chatGptUtil;


    @OnOpen
    public void onOpen(final Session session) {
        try {
            assert session.getId() != null;
        } catch (Exception e) {
            return;
        }
        this.session = session;
        if (chatGptServiceFlux == null) {
            chatGptServiceFlux = (ChatGptServiceFlux) SpringContextUtil.getBean("chatGptServiceFlux");
            chatGptUtil = (ChatGptUtil) SpringContextUtil.getBean("chatGptUtil");
        }
    }

    /**
     * On message.
     *
     * @param message the message
     * @param token   the token
     * @param model   the model
     * @param env     the env
     */
    @OnMessage
    public void onMessage(final String message, final @PathParam("index") Integer index, final @PathParam("env") String env) {
        System.out.println(message);
        final List<ChatGptModel.Messages> messages = JSONObject.parseArray(message, ChatGptModel.Messages.class);

        final ChatGptServiceFlux.FluxOutcome action = chatGptServiceFlux.action(new ChatGptModel().setMessages(messages), index, EnvEnum.fromString(env));

        action.getFlux().doFinally(signal -> handleWebSocketCompletion())
                .subscribe(data -> {
                    final String dataed = chatGptUtil.dataParsing(data);
                    try {
                        this.session.getBasicRemote().sendText(dataed);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    @OnClose
    public void handleWebSocketCompletion() {
        try {
            this.session.close();
        } catch (IOException e) {
            log.error("关闭 WebSocket 会话失败.", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.warn("GPT websocket出现异常 原因:{}", throwable.getMessage());
        //打印堆栈
        //      throwable.printStackTrace();
    }
}
