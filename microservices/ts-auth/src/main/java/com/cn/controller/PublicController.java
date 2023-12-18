package com.cn.controller;

import com.cn.service.ProductPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {


    private final ProductPayService productPayService;


    /**
     * Gets all product card.
     *
     * @return the all product card
     */
    @PostMapping(value = "/alipay/callback", produces = MediaType.APPLICATION_JSON_VALUE)
    public String alipayCallback(HttpServletRequest request) {
        return productPayService.alipayPullback(request);
    }
}
