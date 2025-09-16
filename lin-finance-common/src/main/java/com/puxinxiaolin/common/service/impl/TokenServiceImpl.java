package com.puxinxiaolin.common.service.impl;

import com.puxinxiaolin.common.config.SecurityConfig;
import com.puxinxiaolin.common.constant.CommonConstant;
import com.puxinxiaolin.common.dto.BaseUserInfoDTO;
import com.puxinxiaolin.common.dto.TokenResponse;
import com.puxinxiaolin.common.exception.BizException;
import com.puxinxiaolin.common.exception.LoginException;
import com.puxinxiaolin.common.service.TokenService;
import com.puxinxiaolin.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "sys", name = "enable-my-security", havingValue = "true")
public class TokenServiceImpl<T extends BaseUserInfoDTO> implements TokenService<T> {
    ThreadLocal<T> tokenThreadLocal;
    final RedisTemplate<String, T> redisTemplate;
    final SecurityConfig securityConfig;
    
    /**
     * 是否刷新token（用于操作接口时，是否给token续命）
     *
     * @param tokenResponse
     * @return
     */
    @Override
    public Boolean isRefreshToken(TokenResponse tokenResponse) {
        if (Objects.isNull(tokenResponse)) {
            throw new BizException("token 无效或已过期");
        }

        // token 还有多久过期
        long expireTime = tokenResponse.getExpireDateTime() - DateUtil.getSecondTimeStamp();
        if (expireTime < 0) {
            throw new BizException("token 过期");
        }
        
        // token 已经用了多长时间
        long useTime = tokenResponse.getExpire() - expireTime;
        // 如果超过三分之一则刷新
        return useTime > tokenResponse.getExpire() / 3;
    }

    /**
     * 1、检验token
     * 2、根据规则判断是否要刷新token
     *
     * @param token
     * @return
     */
    @Override
    public T checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new BizException("token 非法");
        }

        T userInfo = redisTemplate.opsForValue().get(CommonConstant.TOKEN_CACHE_KEY + token);
        if (Objects.isNull(userInfo)) {
            throw new BizException("token 不存在或已过期");
        }
        
        TokenResponse tokenResponse = userInfo.getToken();
        Boolean isRefreshToken = isRefreshToken(tokenResponse);
        if (isRefreshToken) {
            tokenResponse.setExpireDateTime(DateUtil.getExpireTimeStamp(tokenResponse.getExpire(), TimeUnit.DAYS));
            redisTemplate.opsForValue()
                    .set(CommonConstant.TOKEN_CACHE_KEY + tokenResponse.getToken(), userInfo, tokenResponse.getExpire(), tokenResponse.getTimeunit());
        }
        return userInfo;
    }

    /**
     * 设置token
     *
     * @param userInfo
     */
    @Override
    public T setToken(T userInfo) {
        if (Objects.isNull(userInfo)) {
            throw new BizException("用户信息不能为空");
        }

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(UUID.randomUUID().toString());
        tokenResponse.setExpireDateTime(DateUtil.getExpireTimeStamp(securityConfig.getExpire(), TimeUnit.DAYS));
        tokenResponse.setExpire(securityConfig.getExpire());
        tokenResponse.setTimeunit(TimeUnit.DAYS);

        T cacheToken = redisTemplate.opsForValue()
                .get(CommonConstant.TOKEN_CACHE_KEY + tokenResponse.getToken());
        if (Objects.nonNull(cacheToken)) {
            throw new BizException("token已存在，不能登录或者注册");
        }
        
        userInfo.setToken(tokenResponse);
        redisTemplate.opsForValue().set(CommonConstant.TOKEN_CACHE_KEY + tokenResponse.getToken(), userInfo, tokenResponse.getExpire(), tokenResponse.getTimeunit());
        return userInfo;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public T getThreadLocalUser() {
        if (tokenThreadLocal == null) {
            return null;
        }
        
        return tokenThreadLocal.get();
    }

    /**
     * 设置用户信息
     *
     * @param userInfo
     */
    @Override
    public void setThreadLocalUser(T userInfo) {
        if (Objects.isNull(tokenThreadLocal)) {
            tokenThreadLocal = new ThreadLocal<>();
        }
        
        tokenThreadLocal.set(userInfo);
    }

    /**
     * 删除本地用户
     */
    @Override
    public void removeThreadLocalUser() {
        if (Objects.nonNull(tokenThreadLocal)) {
            tokenThreadLocal.remove();
        }
    }

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    @Override
    public T getRedisUser(String token) {
        T cacheToken = redisTemplate.opsForValue().get(CommonConstant.TOKEN_CACHE_KEY + token);
        if (Objects.nonNull(cacheToken)) {
            return cacheToken;
        }
        throw new LoginException("获取用户信息异常");
    }

    /**
     * 获取用户id
     *
     * @return
     */
    @Override
    public Long getThreadLocalUserId() {
        T userInfo = getThreadLocalUser();
        if (Objects.nonNull(userInfo) && userInfo.getId() != null && userInfo.getId() > 0L) {
            return userInfo.getId();
        }
        
        throw new BizException("获取用户 ID 异常");
    }

    /**
     * 获取租户id
     *
     * @return
     */
    @Override
    public Long getThreadLocalTenantId() {
        T userInfo = getThreadLocalUser();
        if (Objects.nonNull(userInfo) && userInfo.getTenantId() != null && userInfo.getTenantId() > 0) {
            return userInfo.getTenantId();
        }
        throw new LoginException("获取租户id异常");
    }

    /**
     * 清除token
     *
     * @return
     */
    @Override
    public void clearToken() {
        T userInfo = getThreadLocalUser();
        if (userInfo == null || userInfo.getToken() == null 
                || Strings.isBlank(userInfo.getToken().getToken())) {
            return;
        }
        
        redisTemplate.delete(CommonConstant.TOKEN_CACHE_KEY + userInfo.getToken().getToken());
        log.info("清除 token: {} 成功, 用户 id: {}", userInfo.getToken().getToken(), userInfo.getId());
    }
    
}
