package com.cn.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolExecutorProperties {


    /**
     * 核心线程大小
     */
    private final Integer corePoolSize = 1;

    /**
     * 最大线程数
     */
    private final Integer maximumPoolSize = 1;

    /**
     * 存活时间
     */
    private final Long keepAliveTime = 15L;

}
