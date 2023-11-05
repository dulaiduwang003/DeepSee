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
public class ChatGptDefaultConfiguration {


    @Value("${config.requestUrl}")
    private String requestUrl;

    @Value("${config.lowLevelKeyList}")
    private String[] lowLevelKeyList;

    @Value("${config.seniorKeyList}")
    private String[] seniorKeyList;

    private List<Model> modelList;


    @Data
    @Accessors(chain = true)
    public static class Model {

        private String modelName;

        private Boolean isSeniorModel;

        private Integer frequency;

    }

}
