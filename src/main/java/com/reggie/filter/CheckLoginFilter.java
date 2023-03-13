package com.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.pojo.Employee;
import com.reggie.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class CheckLoginFilter implements Filter {

    private final static AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        log.info("当前访问页面为: {}", req.getRequestURL());
        // 获取uri
        String uri = req.getRequestURI();
        // 直接放行的uri
        String[] noFilterUris = new String[]{
                "/",
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/u",
                "/user/code",
                "/user/login"
                // "/common/**"
        };
        for (String noFilterUri : noFilterUris) {
            if (PATH_MATCHER.match(noFilterUri, uri)) {
                log.info("允许用户访问: {}", uri);
                chain.doFilter(request, response);
                return;
            }
        }
        Employee e = (Employee) (req.getSession().getAttribute("employee"));
        if (e != null) {
            log.info("允许已登录用户访问: {}", uri);
            BaseContext.setUserId(e.getId());
            chain.doFilter(request, response);
            return;
        }

        User user = (User) (req.getSession().getAttribute("user"));
        if(user != null) {
            log.info("允许已登录用户访问: {}", uri);
            BaseContext.setUserId(user.getId());
            chain.doFilter(request, response);
            return;
        }

        log.info("禁止未登录用户访问: {}", uri);
        PrintWriter writer = response.getWriter();
        writer.print(JSON.toJSON(R.error("NOTLOGIN")));
    }
}
