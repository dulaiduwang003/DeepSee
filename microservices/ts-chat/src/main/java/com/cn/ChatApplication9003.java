package com.cn;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChatApplication9003 {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication9003.class, args);
    }

}
