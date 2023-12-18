package com.cn.service;

import com.cn.dto.PayAlipayDto;
import com.cn.vo.AlipayPayCodeVo;
import com.cn.vo.ProductCardVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ProductPayService {

    /**
     * Gets all product card.
     *
     * @return the all product card
     */
    List<ProductCardVo> getAllProductCard();


    /**
     * Gets ali pay status.
     *
     * @param ordersId the orders id
     * @return the ali pay status
     */
    Boolean getAliPayStatus(final String ordersId);


    /**
     * Created order alipay pay code vo.
     *
     * @param productId the product id
     * @return the alipay pay code vo
     */
    AlipayPayCodeVo createdPayOrder(final PayAlipayDto dto) ;


    /**
     * Alipay pullback string.
     *
     * @param request the request
     * @return the string
     */
    String alipayPullback(final HttpServletRequest request);
}
