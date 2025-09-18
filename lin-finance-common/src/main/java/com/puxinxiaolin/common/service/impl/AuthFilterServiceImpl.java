package com.puxinxiaolin.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puxinxiaolin.common.config.SecurityConfig;
import com.puxinxiaolin.common.constant.CommonConstant;
import com.puxinxiaolin.common.dto.BaseUserInfoDTO;
import com.puxinxiaolin.common.exception.BizException;
import com.puxinxiaolin.common.service.AuthFilterService;
import com.puxinxiaolin.common.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.security.auth.login.LoginException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 自定义认证过滤方法
 * @Author: YCcLin
 * @Date: 2025/9/18 22:09
 */
@ConditionalOnProperty(prefix = "sys", name = "enable-my-security", havingValue = "true")
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilterServiceImpl<T> implements AuthFilterService<T> {
    final TokenService<T> tokenService;
    final AntPathMatcher antPathMatcher;
    final SecurityConfig securityConfig;
    final ObjectMapper objectMapper;
    final HandlerExceptionResolver handlerExceptionResolver;
    final RedisTemplate<String, Object> redisTemplate;

    /**
     * 过滤器拦截
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (securityConfig == null || !securityConfig.getEnable()) {
                filterChain.doFilter(request, response);
                return;
            }

            T userInfo = null;
            if ("token".equals(securityConfig.getGetUserType())) {
                String token = request.getHeader("api-access-token");
                userInfo = tokenService.checkToken(token);
            } else if ("gateway".equals(securityConfig.getGetUserType())) {
                String userInfoJson = request.getHeader("user");
                userInfo = objectMapper.readValue(userInfoJson, new TypeReference<T>() {
                });
            }

            if (userInfo == null) {
                throw new LoginException("无法获取到用户信息");
            }
            BaseUserInfoDTO dto = (BaseUserInfoDTO) userInfo;

            // 校验权限
            checkPermissions(dto.getSysRoleIds(), request.getServletPath());
            tokenService.setThreadLocalUser(userInfo);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 走统一的异常处理
            handlerExceptionResolver.resolveException(request, response, null, e);
        } finally {
            // 避免内存泄漏
            tokenService.removeThreadLocalUser();
        }
    }

    /**
     * 不经过过滤器筛选
     *
     * @param request
     * @return
     * @throws ServletException
     */
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        if (securityConfig == null || !securityConfig.getEnable()
                || CollectionUtils.isEmpty(securityConfig.getIgnores())) {
            return false;
        }

        String url = request.getServletPath();
        boolean isMatched = securityConfig.getIgnores().stream()
                .anyMatch(path -> antPathMatcher.match(path, url));
        log.info("请求的路径: {}, 【是否放行: {}】", url, isMatched);
        return isMatched;
    }

    /**
     * 校验权限
     *
     * @param sysRoleIds
     * @param servletPath
     */
    private void checkPermissions(Set<Long> sysRoleIds, String servletPath) {
        if (sysRoleIds.contains(CommonConstant.ROLE_ADMIN)) {
            return;
        }

        // 查询角色绑定的资源路径
        Set<String> roleBindResourcePaths = listRoleResourcePathByCache(sysRoleIds);
        if (CollectionUtils.isEmpty(roleBindResourcePaths)) {
            throw new BizException("角色没有绑定任何资源");
        }

        for (String path : roleBindResourcePaths) {
            if (antPathMatcher.match(path, servletPath)) {
                return;
            }
        }

        throw new BizException("非法访问");
    }

    /**
     * 获取角色绑定的资源路径
     *
     * @param sysRoleIds
     * @return
     */
    private Set<String> listRoleResourcePathByCache(Set<Long> sysRoleIds) {
        // 从 redis 获取角色绑定的资源（菜单）路径
        HashOperations<String, String, Set<String>> hashOperations = redisTemplate.opsForHash();
        List<Set<String>> roleMenuIds = hashOperations.multiGet(CommonConstant.ROLE_RESOURCE_PERMISSIONS, sysRoleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toSet()));

        // List<Set<String>> -> Set<String>
        return roleMenuIds.stream()
                .filter(ids -> !CollectionUtils.isEmpty(ids))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
