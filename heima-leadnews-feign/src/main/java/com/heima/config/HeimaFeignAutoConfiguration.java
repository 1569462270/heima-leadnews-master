package com.heima.config;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : MR.wu
 * @Description : feign配置类
 * @Date : 2022/12/22 14:18
 * @Version : 1.0
 */
@Configuration
@EnableFeignClients(basePackages = "com.heima.feign")
@ComponentScan("com.heima.feign.fallback")
public class HeimaFeignAutoConfiguration {
    @Bean
    Logger.Level level(){
        return Logger.Level.FULL;
    }
}
