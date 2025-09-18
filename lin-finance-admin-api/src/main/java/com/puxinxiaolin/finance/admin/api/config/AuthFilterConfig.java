package com.puxinxiaolin.finance.admin.api.config;

import com.puxinxiaolin.common.service.AuthFilterService;
import com.puxinxiaolin.finance.biz.dto.AdminDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 过滤器配置, 对每次的请求都校验
 * @Author: YCcLin
 * @Date: 2025/9/18 22:37
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthFilterConfig extends OncePerRequestFilter {
    final AuthFilterService<AdminDTO> authFilterService;

    /**
     * 过滤器拦截处理
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        authFilterService.doFilterInternal(request, response, filterChain);
    }

    /**
     * 不经过过滤器筛选
     *
     * @param request current HTTP request
     * @return
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return authFilterService.shouldNotFilter(request);
    }
}
