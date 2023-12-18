package com.cn.service;

import com.cn.enums.DialogueEnum;
import com.cn.service.impl.ChatGptServiceFlux;
import org.springframework.stereotype.Component;

/**
 * The type Operation abstract.
 */
@Component
@SuppressWarnings("all")
public abstract class OperationAbstract<T> {

    /**
     * Action flux.
     *
     * @param t       the t
     * @param envEnum the env enum
     * @return the flux
     */
    public ChatGptServiceFlux.FluxOutcome action(final T t, DialogueEnum dialogueEnum) {

        return switch (dialogueEnum) {
            //识别图片GPT
            case RECOGNITION -> this.streamImageConversations(t);
            //对话GPT
            case DIALOGUE -> this.streamConversations(t);

        };

    }


    /**
     * Streaming conversations chat gpt service flux . flux outcome.
     *
     * @param obj the obj
     * @return the chat gpt service flux . flux outcome
     */
    protected abstract ChatGptServiceFlux.FluxOutcome streamConversations(final T obj);


    /**
     * Stream image conversations chat gpt service flux . flux outcome.
     *
     * @param obj the obj
     * @return the chat gpt service flux . flux outcome
     */
    protected abstract ChatGptServiceFlux.FluxOutcome streamImageConversations(final T obj);

}
