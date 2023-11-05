package com.cn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuthApplication9001 {

    public static void main(String[] args) {

        SpringApplication.run(AuthApplication9001.class, args);
    }
}
