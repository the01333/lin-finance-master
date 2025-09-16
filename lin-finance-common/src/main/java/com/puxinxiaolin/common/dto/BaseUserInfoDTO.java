package com.puxinxiaolin.common.dto;

import lombok.Data;

import java.util.Set;

/**
 * @Description: 用户基本信息
 * @Author: YCcLin
 * @Date: 2025/9/14 23:55
 */
@Data
public class BaseUserInfoDTO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 角色id
     */
    private Set<Long> sysRoleIds;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * token
     */
    private TokenResponse token;

}