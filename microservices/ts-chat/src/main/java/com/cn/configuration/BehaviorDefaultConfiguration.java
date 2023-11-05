package com.cn.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class BehaviorDefaultConfiguration {


    @Value("${behavior.author}")
    private String author;

    @Value("${behavior.botName}")
    private String botName;

    @Value("${behavior.language}")
    private String language;

    @Value("${behavior.sensitiveWords}")
    private String[] sensitiveWords;

    @Value("${behavior.sensitiveSymbol}")
    private String sensitiveSymbol;


}
