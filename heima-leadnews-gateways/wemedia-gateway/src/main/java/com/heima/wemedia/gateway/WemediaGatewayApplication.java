package com.heima.wemedia.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author : MR.wu
 * @Description : 自媒体网关启动类
 * @Date : 2022/12/24 13:59
 * @Version : 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WemediaGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaGatewayApplication.class, args);
    }
}
