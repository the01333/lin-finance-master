package com.puxinxiaolin.finance.biz.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户表绑定微信unionid表（表：member_bind_wx_unionid）
 *
 * @author YCcLin
 */
@Setter
@Getter
public class MemberBindWxUnionId {
    /**
     * 
     */
    private Long id;

    /**
     * 微信unionid
     */
    private String unionId;

    /**
     * 用户id
     */
    private Long memberId;

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

    public void initDefault() {
        if (this.getUnionId() == null) {
            this.setUnionId("");
        }
        if (this.getMemberId() == null) {
            this.setMemberId(0L);
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
    }

    public void initUpdate() {
    }
}