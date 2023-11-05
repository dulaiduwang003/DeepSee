package com.cn.feign;

import com.cn.fallback.AliOssInterfaceFallback;
import com.cn.filter.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 服务调用
 */
@FeignClient(
        name = "oss-server",
        configuration = FeignInterceptor.class,
        fallbackFactory = AliOssInterfaceFallback.class    // 服务降级处理
)
public interface AliOssInterface {


}
