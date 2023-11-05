package com.cn.common;

import com.cn.common.structure.ChatGptStructure;
import com.cn.configuration.ChatGptDefaultConfiguration;
import com.cn.constants.ChatConstant;
import com.cn.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class ChatGptCommon {


    private final ChatGptDefaultConfiguration configuration;
    public static final ChatGptStructure STRUCTURE = new ChatGptStructure();
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final RedisUtils redisUtils;

    /**
     * 轮询获取KEY配置
     *
     * @return the string
     */
    public static ModelObj pollGetKey(final int modelIndex) {
        final List<ChatGptDefaultConfiguration.Model> modelList = STRUCTURE.getModelList();
        //获取具体模型
        ChatGptDefaultConfiguration.Model model = modelList.get(modelIndex);
        if (model == null) {
            model = modelList.get(0);
        }
        //是否为增强模型
        final Boolean isSeniorModel = model.getIsSeniorModel();
        //获取集合KEY
        List<String> keyCistern = (isSeniorModel ? STRUCTURE.getSeniorKeyList() : STRUCTURE.getLowLevelKeyList());
        //遍历获取
        int index = counter.getAndIncrement() % keyCistern.size();
        return new ModelObj()
                //模型名称
                .setModelName(model.getModelName())
                //每日频率
                .setFrequency(model.getFrequency())
                //KEY
                .setKey(keyCistern.get(index));
    }

    /**
     * 初始化.
     */
    @PostConstruct
    public void init() {
        final ChatGptStructure value = (ChatGptStructure) redisUtils.getValue(ChatConstant.CONFIG);
        if (value != null) {
            STRUCTURE
                    .setSeniorKeyList(value.getSeniorKeyList())
                    .setLowLevelKeyList(value.getLowLevelKeyList())
                    .setModelList(value.getModelList())
                    .setRequestUrl(value.getRequestUrl())
                    .setModelList(value.getModelList());

        } else
            STRUCTURE
                    .setSeniorKeyList(Arrays.asList(configuration.getSeniorKeyList()))
                    .setLowLevelKeyList(Arrays.asList(configuration.getLowLevelKeyList()))
                    .setModelList(configuration.getModelList())
                    .setRequestUrl(configuration.getRequestUrl());

    }

    /**
     * 获取配置
     *
     * @return the config
     */
    public static ChatGptStructure getConfig() {
        return STRUCTURE;
    }

    /**
     * 更新线上配置
     *
     * @param structure the structure
     */
    public static void updateOnlineConfig(final ChatGptStructure structure) {
        STRUCTURE
                .setSeniorKeyList(structure.getSeniorKeyList())
                .setLowLevelKeyList(structure.getLowLevelKeyList())
                .setRequestUrl(structure.getRequestUrl())
                .setModelList(structure.getModelList());
    }


    @Data
    @Accessors(chain = true)
    public static class ModelObj {

        private String key;

        private String modelName;

        private Integer frequency;

    }

}
