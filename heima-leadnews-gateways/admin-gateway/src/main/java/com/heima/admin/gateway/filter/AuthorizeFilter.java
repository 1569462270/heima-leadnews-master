package com.heima.admin.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.heima.admin.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : MR.wu
 * @Description : 认证过滤器
 * @Date : 2022/12/21 10:46
 * @Version : 1.0
 */
@Component
@Slf4j
@Order(0)
public class AuthorizeFilter implements GlobalFilter {
    // 初始化白名单
    private static List<String> urlList = new ArrayList<>();

    static {
        urlList.add("/login/in");
        urlList.add("/v2/api-docs");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        for (String url : urlList) {
            if (path.contains(url)) {
                return chain.filter(exchange);
            }
        }
        // 获取token
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isBlank(token)) {
            return writeMessage(exchange, "需要登录");
        }
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int i = AppJwtUtil.verifyToken(claimsBody);
            if (i > 0) {
                return writeMessage(exchange, "认证失败，请重新登录");
            }
            Long id = claimsBody.get("id", Long.class);
            log.info("token网关校验成功id:{},URL:{}", id, request.getURI().getPath());
            request.mutate().header("userId", id.toString());
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("token解析失败，失败原因：{}", e.getMessage());
            return writeMessage(exchange, "认证失效，请重新登录");
        }
    }

    private Mono<Void> writeMessage(ServerWebExchange exchange, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("errorMessage", message);
        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 设置状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 设置返回类型
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 设置返回数据
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONBytes(map));
        // 响应数据回浏览器
        return response.writeWith(Flux.just(buffer));
    }
}
