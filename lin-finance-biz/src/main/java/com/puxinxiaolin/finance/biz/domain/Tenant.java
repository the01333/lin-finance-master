package com.puxinxiaolin.finance.biz.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 租户表（表：tenant）
 *
 * @author YCcLin
 */
@Setter
@Getter
public class Tenant {
    /**
     * 
     */
    private Long id;

    /**
     * 租户名称
     */
    private String name;

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
     * 管理员id
     */
    private Long adminId;

    /**
     * 修改管理员id
     */
    private Long updateAdminId;

    /**
     * 是否删除，0：删除，1：未删除
     */
    private Boolean delFlag;

    public void initDefault() {
        if (this.getName() == null) {
            this.setName("");
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
        if (this.getAdminId() == null) {
            this.setAdminId(0L);
        }
        if (this.getUpdateAdminId() == null) {
            this.setUpdateAdminId(0L);
        }
        if (this.getDelFlag() == null) {
            this.setDelFlag(false);
        }
    }

    public void initUpdate() {
    }
}