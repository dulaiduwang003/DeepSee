package com.cn.utils;

import com.alibaba.fastjson.JSONObject;
import com.cn.exceptions.WechatException;
import com.cn.model.WeChaQrCodeModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

/**
 * 微信工具类
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 * @date 2022/6/11 14:23
 */
@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
@Slf4j
public class WeChatUtils {
    @Value("${we-chat.appId}")
    private String appId;
    @Value("${we-chat.secret}")
    private String secret;
    @Value("${we-chat.env}")
    private String env;

    private final RedisUtils redisUtils;
    private static final WebClient WEB_CLIENT = WebClient.builder().build();


    public String getOpenId(final String code) {
        try {
            final String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + this.appId +
                    "&secret=" + this.secret + "&js_code=" + code + "&grant_type=authorization_code";
            final String response = WEB_CLIENT.get().uri(url).retrieve().bodyToMono(String.class).block();
            final JSONObject block = JSONObject.parseObject(response);

            final String openid = block.getString("openid");
            if (!StringUtils.notEmpty(openid)) {
                throw new RuntimeException();
            }
            return openid;
        } catch (Exception e) {
            log.error("获取微信用户标识失败 信息:{},错误类:{}", e.getMessage(), e.getClass());
            throw new WechatException("获取微信用户标识失败!请稍后再试");
        }
    }

    public String getQrCode(final String secene) {

        try {
            final String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + WeChatTokenUtil.INSTANCE.getWechatToken(appId, secret);
            final byte[] block = WEB_CLIENT.post()
                    .uri(url)
                    .body(BodyInserters.fromValue(new WeChaQrCodeModel().setScene(secene).setEnv_version(env)))
                    .retrieve().bodyToMono(byte[].class).block();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(block);
        } catch (Exception e) {
            log.error("获取小程序二维码失败 信息:{},错误类:{}", e.getMessage(), e.getClass());
            throw new WechatException("获取小程序二维码失败!请稍后再试");
        }
    }

}

