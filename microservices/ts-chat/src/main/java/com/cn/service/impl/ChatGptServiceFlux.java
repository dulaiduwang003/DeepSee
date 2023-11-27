package com.cn.service.impl;

import com.cn.common.ChatGptCommon;
import com.cn.model.ChatGptModel;
import com.cn.service.OperationAbstract;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * The type Dialogue service.
 */
@Service
@RequiredArgsConstructor
public class ChatGptServiceFlux extends OperationAbstract<ChatGptModel> {

    @Override
    protected FluxOutcome establishConnectionWeb(final ChatGptModel obj, final int modelIndex) {

        //获取当前指向模型
        final ChatGptCommon.ModelObj modelObj = ChatGptCommon.pollGetKey(modelIndex);

        final Flux<String> stringFlux =  WebClient.builder().baseUrl(ChatGptCommon.STRUCTURE.getRequestUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + modelObj.getKey()).build()
                .post()
                .uri("/chat/completions")
                .body(BodyInserters.fromValue(
                        //封装MODEL
                        new ChatGptModel().setMessages(obj.getMessages()).setModel(modelObj.getModelName())))
                .retrieve()
                .bodyToFlux(String.class);

        return new FluxOutcome()
                .setFlux(stringFlux).setVerify(false);
    }

    @Override
    protected FluxOutcome establishConnectionWechat(final ChatGptModel obj, final int modelIndex) {
        return null;
    }


    /**
     * The type Flux outcome.
     */
    @Data
    @Accessors(chain = true)
    public static class FluxOutcome {

        private Flux<String> flux;

        private boolean isVerify;
    }
}
