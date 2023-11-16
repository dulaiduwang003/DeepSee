package com.cn.common;

import com.cn.common.structure.DallStructure;
import com.cn.configuration.DallDefaultConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class DallCommon {


    private final DallDefaultConfiguration configuration;
    public static final DallStructure STRUCTURE = new DallStructure();
    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 轮询获取KEY配置
     *
     * @return the string
     */
    public static String pollGetKey() {
        //获取集合KEY
        List<String> keyCistern =STRUCTURE.getKeyList();
        //遍历获取
        int index = counter.getAndIncrement() % keyCistern.size();
        return keyCistern.get(index);
    }

    /**
     * 初始化.
     */
    @PostConstruct
    public void init() {
        STRUCTURE
                .setKeyList(Arrays.asList(configuration.getKeyList()))
                .setRequestUrl(configuration.getRequestUrl());
    }

    /**
     * 获取配置
     *
     * @return the config
     */
    public static DallStructure getConfig() {
        return STRUCTURE;
    }


}
