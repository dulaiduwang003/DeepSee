package com.cn.common;

import com.cn.common.structure.PoolStructure;
import com.cn.configuration.PoolDefaultConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PoolCommon {


    private final PoolDefaultConfiguration configuration;
    public static final PoolStructure STRUCTURE = new PoolStructure();

    /**
     * 初始化.
     */
    @PostConstruct
    public void init() {
        STRUCTURE
                .setDallConcurrent(configuration.getDallConcurrent())
                .setMaximumTask(configuration.getMaximumTask())
                .setSdConcurrent(configuration.getSdConcurrent());
    }


}
