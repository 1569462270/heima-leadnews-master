package com.heima.user.filter;

import com.heima.model.threadlocal.AppThreadLocalUtils;
import com.heima.model.user.entity.ApUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author : MR.wu
 * @Description : TODO
 * @Date : 2022/12/29 19:42
 * @Version : 1.0
 */
@Slf4j
@Order(1)
@WebFilter(filterName = "appTokenFilter", urlPatterns = "/*")
@Component
public class AppTokenFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 获取请求对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 查看请求header中是否有userId属性
        String userId = request.getHeader("userId");// 如果是设备登录 存的userId是0
        // 如果userId有值存入到ThreadLocal中
        if (StringUtils.isNotBlank(userId) && Integer.valueOf(userId) != 0) {
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            AppThreadLocalUtils.setUser(apUser);
        }
        // 放行
        filterChain.doFilter(servletRequest, servletResponse);

        // 清空登录信息
        AppThreadLocalUtils.clear();
    }
}
