package com.cn.common;

import com.cn.common.structure.BehaviorStructure;
import com.cn.configuration.BehaviorDefaultConfiguration;
import com.cn.constants.ChatConstant;
import com.cn.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class BehaviorCommon {


    private final BehaviorDefaultConfiguration configuration;
    private static final BehaviorStructure STRUCTURE = new BehaviorStructure();

    private final RedisUtils redisUtils;

    /**
     * 初始化.
     */
    @PostConstruct
    public void init() {
        final BehaviorStructure value = (BehaviorStructure) redisUtils.getValue(ChatConstant.BEHAVIOR);
        if (value != null) {
            STRUCTURE.setAuthor(value.getAuthor())
                    .setLanguage(value.getLanguage())
                    .setBotName(value.getBotName())
                    .setSensitiveWords(value.getSensitiveWords())
                    .setSensitiveSymbol(value.getSensitiveSymbol());

        } else
            STRUCTURE.setAuthor(configuration.getAuthor())
                    .setLanguage(configuration.getLanguage())
                    .setBotName(configuration.getBotName())
                    .setSensitiveWords(Arrays.asList(configuration.getSensitiveWords()))
                    .setSensitiveSymbol(configuration.getSensitiveSymbol());

    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public static BehaviorStructure getConfig() {
        return STRUCTURE;
    }

    /**
     * 更新线上配置
     *
     * @param structure the structure
     */
    public static void updateOnlineConfig(final BehaviorStructure structure) {
        STRUCTURE.setAuthor(structure.getAuthor())
                .setLanguage(structure.getLanguage())
                .setBotName(structure.getBotName())
                .setSensitiveWords(structure.getSensitiveWords())
                .setSensitiveSymbol(structure.getSensitiveSymbol());

    }


}
