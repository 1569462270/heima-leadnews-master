package com.heima.wemedia.filter;

import com.heima.model.threadlocal.WmThreadLocalUtils;
import com.heima.model.wemedia.entity.WmUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author : MR.wu
 * @Description : 自媒体过滤器
 * @Date : 2022/12/24 14:04
 * @Version : 1.0
 */
@Order(1)
@WebFilter(filterName = "wmTokenFilter", urlPatterns = "/*")
@Slf4j
@Component
public class WmTokenFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 得到header中的信息
        String userId = request.getHeader("userId");
        if (userId != null) {
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.valueOf(userId));
            // 保存到当前线程中
            WmThreadLocalUtils.setUser(wmUser);
        }
        // 如果没有则直接放行
        filterChain.doFilter(request, response);
        // 过滤器处理完毕后  清空用户信息
        WmThreadLocalUtils.clear();
    }
}
