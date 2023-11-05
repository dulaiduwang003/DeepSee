package com.cn.fallback;

import com.cn.feign.AliOssInterface;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class AliOssInterfaceFallback implements FallbackFactory<AliOssInterface> {

    @Override
    public AliOssInterface create(Throwable throwable) {
        return new AliOssInterface() {

        };
    }
}
