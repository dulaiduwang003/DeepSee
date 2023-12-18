package com.cn.net;

import com.alibaba.fastjson.JSONObject;
import com.cn.constants.ChatConstant;
import com.cn.dto.DialogueParameterDto;
import com.cn.enums.DialogueEnum;

import com.cn.exception.CloseException;
import com.cn.service.impl.ChatGptServiceFlux;
import com.cn.utils.ChatGptUtil;
import com.cn.utils.SpringContextUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * The type Chat net.
 */
@Slf4j
@ServerEndpoint(value = "/chat/{token}", subprotocols = {"protocol"})
@SuppressWarnings("all")
@Service
public class ChatGptNet {

    private static ChatGptServiceFlux chatGptServiceFlux;
    private static ChatGptUtil chatGptUtil;
    private Session session;

    private int maxSize = 10 * 1024 * 1024;

    @OnOpen
    public void onOpen(final Session session) {
        if (session.getMaxTextMessageBufferSize() <= 8192) {
            session.setMaxBinaryMessageBufferSize(maxSize);
            session.setMaxTextMessageBufferSize(maxSize);
        }
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
    public void onMessage(final String parameter) {
        final DialogueParameterDto dto = JSONObject.parseObject(parameter, DialogueParameterDto.class);

        final ChatGptServiceFlux.FluxOutcome action = chatGptServiceFlux.action(dto, DialogueEnum.fromString(dto.getType()));

        action.getFlux().doFinally(signal -> handleWebSocketCompletion())
                .subscribe(data -> {
                    final String dataed = chatGptUtil.dataParsing(data);
                    try {
                        this.session.getBasicRemote().sendText(dataed);
                    } catch (Exception e) {
                        //用户可能手动端口连接
                        throw new CloseException();
                    }
                }, throwable -> {
                    //为 Close异常时 过滤
                    if (!(throwable instanceof CloseException)) {
                        try {
                            this.session.getBasicRemote().sendText(ChatConstant.ERROR);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            throwable.printStackTrace();
                            log.error("调用GPT时出现异常 异常信息:{} ", throwable.getMessage());
                        }
                    }
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
