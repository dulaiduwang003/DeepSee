package com.cn.feign.drawing;

import com.cn.fallback.DrawingInterfaceFallback;
import com.cn.filter.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 服务调用
 */
@FeignClient(
        value = "drawing-server",
        configuration = FeignInterceptor.class,
        fallbackFactory = DrawingInterfaceFallback.class
)
public interface DrawingServerInterface {



}
