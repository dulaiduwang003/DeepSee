package com.cn.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.constant.OrdersStatusConstant;
import com.cn.dto.PayAlipayDto;
import com.cn.entity.TsOrders;
import com.cn.entity.TsProductCard;
import com.cn.exception.OrdersException;
import com.cn.mapper.TsOrdersMapper;
import com.cn.mapper.TsProductCardMapper;
import com.cn.queue.UnpaidOrderQueue;
import com.cn.service.ProductPayService;
import com.cn.utils.*;
import com.cn.vo.AlipayPayCodeVo;
import com.cn.vo.ProductCardVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cn.constant.OrdersConstant.ORDER_CREATED_LOCK;
import static com.cn.constant.OrdersConstant.ORDER_PAY_TYPE;
import static com.cn.constant.OrdersStatusConstant.WAIT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPayServiceImpl implements ProductPayService {

    private final TsProductCardMapper tsProductCardMapper;

    private final RedisLockHelper redisLockHelper;

    private final IdGeneratorUtils idGeneratorUtils;

    private final TsOrdersMapper tsOrdersMapper;

    private final RedisUtils redisUtils;

    private final UnpaidOrderQueue unpaidOrderQueue;

    @Value("${ali-pay.appId}")
    private String appId;

    @Value("${ali-pay.alipayPublicKey}")
    private String alipayPublicKey;

    @Value("${ali-pay.privateKey}")
    private String privateKey;

    @Value("${ali-pay.domain}")
    private String domain;

    @Override
    public AlipayPayCodeVo createdPayOrder(final PayAlipayDto dto) {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        //当前登录用户ID
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        //锁前缀
        final String lockPrefix = ORDER_CREATED_LOCK + currentLoginId;
        //上锁
        final boolean lock = redisLockHelper.lock(lockPrefix, timestamp);

        if (!lock) {
            throw new OrdersException("操作繁忙,请勿重复下单", 500);
        }

        try {

            final TsProductCard tsProductCard = tsProductCardMapper.selectOne(new QueryWrapper<TsProductCard>()
                    .lambda()
                    .eq(TsProductCard::getProductCardId, dto.getProductCardId())
                    .select(TsProductCard::getProductName,
                            TsProductCard::getDays,
                            TsProductCard::getPrice
                    )
            );

            if (tsProductCard == null) {
                throw new OrdersException("商品不存在或已被下架");
            }
            final String ordersId = idGeneratorUtils.getOrderNo();

            final TsOrders tsOrders = new TsOrders()
                    .setOrdersId(ordersId)
                    .setPrice(tsProductCard.getPrice())
                    .setStatus(WAIT)
                    .setProductName(tsProductCard.getProductName())
                    .setUserId(currentLoginId);
            tsOrdersMapper.insert(tsOrders);
            //装载配置
            final AlipayConfig alipayConfig = new AlipayConfig();
            alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
            alipayConfig.setFormat("json");
            alipayConfig.setCharset("UTF8");
            alipayConfig.setSignType("RSA2");
            alipayConfig.setAppId(appId);
            alipayConfig.setAlipayPublicKey(alipayPublicKey);
            alipayConfig.setPrivateKey(privateKey);

            //构建支付宝订单
            final AlipayTradePrecreateResponse response = getAlipayTradePrecreateResponse(alipayConfig, ordersId, tsProductCard);
            if (response.isSuccess()) {
                //添加至 待支付 队列中
                unpaidOrderQueue.add(ordersId);
                redisUtils.setValueTimeout(ORDER_PAY_TYPE + ordersId, false, 600);
                //返回base64编码支付二维码图片
                return new AlipayPayCodeVo()
                        .setOrdersId(ordersId)
                        .setQrCode(QRCodeGenerator.generateQRCode(response.getQrCode()))
                        .setPrice(tsProductCard.getPrice())
                        .setProductName(tsProductCard.getProductName())
                        .setCreatedTime(LocalDateTime.now());
            }
            throw new RuntimeException();
        } catch (Exception e) {
            log.error("生成支付宝支付二维码失败:{}", e.getMessage());
            throw new OrdersException("创建支付二维码失败,请稍后重试");
        } finally {
            redisLockHelper.unlock(lockPrefix, timestamp);
        }

    }

    /**
     * Alipay pullback string.
     *
     * @param request the request
     * @return the string
     */
    @Override
    public String alipayPullback(final HttpServletRequest request) {
        final Map<String, String> params = getStringMap(request);
        // 调用SDK验证签名
        boolean signVerified;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF8", "RSA2");
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        // 验证成功
        if (signVerified) {
            String tradeStatus = request.getParameter("trade_status");
            log.info("回调结果:{}", tradeStatus);
            // 支付成功
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                final String outTradeNo = request.getParameter("out_trade_no");
                final TsOrders orders = tsOrdersMapper.selectOne(new QueryWrapper<TsOrders>()
                        .lambda()
                        .eq(TsOrders::getOrdersId, outTradeNo)
                );
                if (orders != null) {
                    tsOrdersMapper
                            .updateById(new TsOrders()
                                    .setOrdersId(outTradeNo)
                                    //已支付
                                    .setStatus(OrdersStatusConstant.SUCCEED)
                            );
                    redisUtils.setValueTimeout(ORDER_PAY_TYPE + outTradeNo, true, 600);
                }
                return "success";
            }
        } else {
            log.error("支付失败");
            return "fail";
        }
        return "fail";
    }

    /**
     * Gets string map.
     *
     * @param request the request
     * @return the string map
     */
    @NotNull
    private static Map<String, String> getStringMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append((i == values.length - 1) ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        return params;
    }

    /**
     * Gets alipay trade recreate response.
     *
     * @param alipayConfig  the alipay config
     * @param ordersId      the orders id
     * @param tsProductCard the ts product card
     * @return the alipay trade recreate response
     * @throws AlipayApiException the alipay api exception
     */
    private AlipayTradePrecreateResponse getAlipayTradePrecreateResponse(AlipayConfig alipayConfig, String ordersId, TsProductCard tsProductCard) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        //预构建请求
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(ordersId);
        //支付金额
        model.setTotalAmount(String.valueOf(tsProductCard.getPrice()));
        //商品名称
        model.setSubject(tsProductCard.getProductName());
        //5分钟过期
        model.setTimeoutExpress("5m");
        request.setBizModel(model);
        //支付宝回调地址
        request.setNotifyUrl(domain + "/auth-api/public/alipay/callback");
        return alipayClient.execute(request);
    }

    @Override
    public Boolean getAliPayStatus(final String ordersId) {
        final Object value = redisUtils.getValue(ORDER_PAY_TYPE + ordersId);
        if (value == null) {
            throw new OrdersException("长时间未支付,订单已被取消");
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @Override
    public List<ProductCardVo> getAllProductCard() {
        return tsProductCardMapper.selectList(new QueryWrapper<TsProductCard>()
                .lambda()
                .select(TsProductCard::getProductCardId,
                        TsProductCard::getDays,
                        TsProductCard::getPrice,
                        TsProductCard::getProductName
                )
        ).stream().map(s -> new ProductCardVo()
                .setProductCardId(s.getProductCardId())
                .setProductName(s.getProductName())
                .setPrice(s.getPrice())
                .setDays(s.getDays())).toList();

    }
}
