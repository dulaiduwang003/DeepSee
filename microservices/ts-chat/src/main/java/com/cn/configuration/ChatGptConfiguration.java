package com.cn.configuration;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "config")
@Data
public class ChatGptConfiguration {

    @Value("${config.requestUrl}")
    private String requestUrl;

    @Value("${config.lowLevelKeyList}")
    private String[] lowLevelKeyList;

    @Value("${config.seniorKeyList}")
    private String[] seniorKeyList;

    private List<Model> modelList;

    private ImageRecognitionConfig imageRecognitionConfig;


    @Data
    @Accessors(chain = true)
    public static class Model {

        private String modelName;

        private Boolean isSeniorModel;

        private Integer top_p;

        private Integer max_tokens;

        private Integer temperature;
    }

    @Data
    @Accessors(chain = true)
    public static class ImageRecognitionConfig {

        private String model;

        private String detail;

        private Integer max_tokens;
    }


}
