package com.cn.fallback;

import com.cn.feign.drawing.DrawingServerInterface;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class DrawingInterfaceFallback implements FallbackFactory<DrawingServerInterface> {

    @Override
    public DrawingServerInterface create(Throwable throwable) {
        return new DrawingServerInterface() {



        };
    }
}
