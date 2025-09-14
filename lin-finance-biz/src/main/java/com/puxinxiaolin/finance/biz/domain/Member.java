package com.puxinxiaolin.finance.biz.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户表（表：member）
 *
 * @author YCcLin
 */
@Setter
@Getter
public class Member {
    /**
     * 
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 是否禁用
     */
    private Boolean disable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 角色id，多个以英文逗号分隔
     */
    private String sysRoleIds;

    public void initDefault() {
        if (this.getNickName() == null) {
            this.setNickName("");
        }
        if (this.getDisable() == null) {
            this.setDisable(false);
        }
        if (this.getCreateTime() == null) {
            this.setCreateTime(new Date());
        }
        if (this.getUpdateTime() == null) {
            this.setUpdateTime(new Date());
        }
        if (this.getName() == null) {
            this.setName("");
        }
        if (this.getAvatarUrl() == null) {
            this.setAvatarUrl("");
        }
        if (this.getTenantId() == null) {
            this.setTenantId(0L);
        }
        if (this.getEmail() == null) {
            this.setEmail("");
        }
    }

    public void initUpdate() {
    }
}