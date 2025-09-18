package com.puxinxiaolin.wx.constant;

/**
 * @Description: 微信接口常量类
 * @Author: YCcLin
 * @Date: 2025/9/16 23:29
 */
public class WxApiConstant {

    /**
     * 获取 access_token
     */
    public static final String ACCESS_TOKEN_API = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 生成临时公众号二维码
     * 文档地址: https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
     */
    public static final String MP_QRCODE_CREATE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
    
}
