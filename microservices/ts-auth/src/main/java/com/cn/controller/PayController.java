package com.cn.controller;

import com.cn.dto.PayAlipayDto;
import com.cn.msg.Result;
import com.cn.service.ProductPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {


    /**
     * The Product pay service.
     */
    private final ProductPayService productPayService;


    /**
     * Gets all product card.
     *
     * @return the all product card
     */
    @PostMapping(value = "/created/alipay", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result createdAlipay(@RequestBody @Validated PayAlipayDto dto) {
     return Result.data(productPayService.createdPayOrder(dto));
    }

    /**
     * Gets all product card.
     *
     * @return the all product card
     */
    @GetMapping(value = "/orders/status/{ordersId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getAlipayStatus(@PathVariable String ordersId) {
        return Result.data(productPayService.getAliPayStatus(ordersId));
    }



    /**
     * Gets all product card.
     *
     * @return the all product card
     */
    @GetMapping(value = "/get/all/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getAllProductCard() {
        return Result.data(productPayService.getAllProductCard());
    }
}
