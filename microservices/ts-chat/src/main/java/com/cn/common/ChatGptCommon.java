package com.cn.common;

import com.cn.common.structure.ChatGptStructure;
import com.cn.configuration.ChatGptConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@RefreshScope
public class ChatGptCommon {


    private final ChatGptConfiguration configuration;
    public static final ChatGptStructure STRUCTURE = new ChatGptStructure();
    private static final AtomicInteger counter = new AtomicInteger(0);


    /**
     * 轮询获取KEY配置
     *
     * @return the string
     */
    public static ModelObj pollGetKey(final Integer modelIndex, final boolean isAuto) {
        ChatGptConfiguration.Model model = null;
        Boolean isSeniorModel = isAuto;
        if (modelIndex != null) {
            final List<ChatGptConfiguration.Model> modelList = STRUCTURE.getModelList();
            //获取具体模型
            model = modelList.get(modelIndex);
            if (model == null) {
                model = modelList.get(0);
            }
            //是否为增强模型
            isSeniorModel = model.getIsSeniorModel();
        }
        //获取集合KEY
        List<String> keyCistern = (isSeniorModel ? STRUCTURE.getSeniorKeyList() : STRUCTURE.getLowLevelKeyList());
        //遍历获取
        int index = counter.getAndIncrement() % keyCistern.size();
        final ModelObj modelObj = new ModelObj()
                //KEY
                .setKey(keyCistern.get(index));

        if (model != null) {
            //模型名称
            modelObj
                    .setTemperature(model.getTemperature())
                    .setMax_tokens(model.getMax_tokens())
                    .setTop_p(model.getTop_p())
                    .setModelName(model.getModelName());
        }

        return modelObj;
    }

    /**
     * 初始化.
     */
    @PostConstruct
    public void init() {

        STRUCTURE
                .setImageRecognitionConfig(configuration.getImageRecognitionConfig())
                .setSeniorKeyList(Arrays.asList(configuration.getSeniorKeyList()))
                .setLowLevelKeyList(Arrays.asList(configuration.getLowLevelKeyList()))
                .setModelList(configuration.getModelList())
                .setRequestUrl(configuration.getRequestUrl());
    }


    @Data
    @Accessors(chain = true)
    public static class ModelObj {

        private String key;

        private String modelName;

        private Integer top_p;

        private Integer max_tokens;

        private Integer temperature;


    }

}
