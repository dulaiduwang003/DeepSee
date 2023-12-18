package com.cn.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "config.pool")
@Data
public class PoolDefaultConfiguration {


    private Integer maximumTask;

    private Integer sdConcurrent;

    private Integer dallConcurrent;

}
