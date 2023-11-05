package com.cn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GateWayApplication9000 {

    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication9000.class, args);
    }
}
