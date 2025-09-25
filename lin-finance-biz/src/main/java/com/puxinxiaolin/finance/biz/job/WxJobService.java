package com.puxinxiaolin.finance.biz.job;

import com.puxinxiaolin.wx.config.WxConfig;
import com.puxinxiaolin.wx.service.WxService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 微信定时任务
 * @Author: YCcLin
 * @Date: 2025/9/25 21:22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxJobService {
    final WxService wxService;
    final WxConfig wxConfig;

    /**
     * 设置公众号的 access_token 到缓存中
     *
     * @param param
     * @return
     */
    @XxlJob("setMpAccessTokenCacheJobHandler")
    public ReturnT<String> setMpAccessTokenCacheJobHandler(String param) {
        XxlJobHelper.log("setMpAccessTokenCacheJobHandler -> begin");

        try {
            XxlJobHelper.log("开始执行任务");
            wxService.setMpAccessTokenCache(wxConfig.getMp().getAppId(), wxConfig.getMp().getSecret());
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            XxlJobHelper.log("任务执行失败", e);
            return ReturnT.FAIL;
        }

        return ReturnT.SUCCESS;
    }

}
