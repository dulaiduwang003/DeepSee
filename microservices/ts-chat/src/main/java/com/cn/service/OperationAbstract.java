package com.cn.service;

import com.cn.enums.EnvEnum;
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
    public ChatGptServiceFlux.FluxOutcome action(final T t, final int modelIndex, final EnvEnum envEnum) {
        return switch (envEnum) {
            case WEB -> this.establishConnectionWeb(t, modelIndex);
            default -> this.establishConnectionWeb(t, modelIndex);
        };

    }

    /**
     * Establish connection web chat gpt service flux . flux outcome.
     *
     * @param obj the obj
     * @return the chat gpt service flux . flux outcome
     */
    protected abstract ChatGptServiceFlux.FluxOutcome establishConnectionWeb(final T obj, final int modelIndex);

    /**
     * Establish connection wechat chat gpt service flux . flux outcome.
     *
     * @param obj the obj
     * @return the chat gpt service flux . flux outcome
     */
    protected abstract ChatGptServiceFlux.FluxOutcome establishConnectionWechat(final T obj, final int modelIndex);
}
