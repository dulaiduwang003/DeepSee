package com.cn.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "config.dall")
@Data
public class DallDefaultConfiguration {

    private String requestUrl;

    private String[] keyList;

}
