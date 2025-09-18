package com.puxinxiaolin.finance.biz.service;

import com.puxinxiaolin.common.dto.TokenResponse;
import com.puxinxiaolin.finance.biz.dto.form.PhoneRegisterForm;
import com.puxinxiaolin.finance.biz.dto.vo.GenerateMpRegCodeVo;
import com.puxinxiaolin.wx.dto.MpSubscribeEventRequest;

public interface MemberRegService {

    /**
     * 手机号注册
     *
     * @param form
     * @return
     */
    Long phoneReg(PhoneRegisterForm form);

    /**
     * 生成微信公众号二维码
     *
     * @param clientId
     * @return
     */
    GenerateMpRegCodeVo generateMpRegCode(String clientId);

    /**
     * 处理微信公众号关注事件
     *
     * @param request
     */
    void handleMpSubscribeEventRequest(MpSubscribeEventRequest request);

    /**
     * 通过 openId 注册
     *
     * @param appId
     * @param clientId
     * @param toUserName
     * @return
     */
    TokenResponse registerByOpenId(String appId, String clientId, String toUserName);

    /**
     * 扫码注册（通过 appId 和 openId 注册）
     *
     * @param appId
     * @param openId
     * @return
     */
    Long scReg(String appId, String openId);

}
