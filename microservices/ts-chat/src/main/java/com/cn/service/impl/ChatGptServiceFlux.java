package com.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.ChatGptCommon;
import com.cn.configuration.ChatGptConfiguration;
import com.cn.dto.DialogueParameterDto;
import com.cn.model.ChatGptDialogueModel;
import com.cn.model.ChatGptImageRecognitionModel;
import com.cn.service.OperationAbstract;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * The type Dialogue service.
 */
@Service
@RequiredArgsConstructor
public class ChatGptServiceFlux extends OperationAbstract<DialogueParameterDto> {

    @Override
    protected FluxOutcome streamConversations(final DialogueParameterDto obj) {
        //获取额外参数
        final DialogueParameterDto.Extra extra = JSONObject.parseObject(obj.getExtra(), DialogueParameterDto.Extra.class);
        //获取数据集
        final List<ChatGptDialogueModel.Messages> messages = JSONObject.parseArray(obj.getMessages(), ChatGptDialogueModel.Messages.class);
        //获取当前指向模型
        final ChatGptCommon.ModelObj modelObj = ChatGptCommon.pollGetKey(extra.getModelIndex(), false);


        final Flux<String> stringFlux = WebClient.builder().baseUrl(ChatGptCommon.STRUCTURE.getRequestUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + modelObj.getKey()).build()
                .post()
                .uri("/chat/completions")
                .body(BodyInserters.fromValue(
                        //封装MODEL
                        new ChatGptDialogueModel()
                                .setMessages(messages)
                                .setMax_tokens(modelObj.getMax_tokens())
                                .setTemperature(modelObj.getTemperature())
                                .setTop_p(modelObj.getTop_p())
                                .setModel(modelObj.getModelName())))
                .retrieve()
                .bodyToFlux(String.class);

        return new FluxOutcome()
                .setFlux(stringFlux).setVerify(extra.getIsFiltration());
    }


    @Override
    protected FluxOutcome streamImageConversations(final DialogueParameterDto obj) {

        final List<ChatGptImageRecognitionModel.Messages> messages = JSONObject.parseArray(obj.getMessages(), ChatGptImageRecognitionModel.Messages.class);
        //获取额外参数
        final DialogueParameterDto.Extra extra = JSONObject.parseObject(obj.getExtra(), DialogueParameterDto.Extra.class);

        final ChatGptConfiguration.ImageRecognitionConfig config = ChatGptCommon.STRUCTURE.getImageRecognitionConfig();
        //某个角落 就你和我
        final Flux<String> stringFlux = WebClient.builder().baseUrl(ChatGptCommon.STRUCTURE.getRequestUrl())
                //从前从前
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + ChatGptCommon.pollGetKey(null, true).getKey()).build()
                .post()
                //像突然土壤抓紧花的迷惑
                .uri("/chat/completions")
                .body(BodyInserters.fromValue(
                        new ChatGptImageRecognitionModel()
                                .setModel(config.getModel())
                                .setMax_tokens(config.getMax_tokens())
                                .setMessages(messages)
                ))
                .retrieve()
                //但偏偏 雨渐渐 大到我看你不见
                .bodyToFlux(String.class);

        return new FluxOutcome()
                .setFlux(stringFlux).setVerify(extra.getIsFiltration());
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
