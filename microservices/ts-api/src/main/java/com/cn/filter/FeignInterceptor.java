package com.cn.filter;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * The type Feign interceptor.
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        requestTemplate.header(StpUtil.getTokenName(), StpUtil.getTokenValue());
    }
}
