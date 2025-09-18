package com.puxinxiaolin.wx;

import com.puxinxiaolin.wx.dto.AccessTokenResult;
import com.puxinxiaolin.wx.service.WxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
@RequiredArgsConstructor
public class WxTest {
    @Resource
    private WxService wxService;
    
    @Test
    public void testGetMpAccessToken() {
        AccessTokenResult accessTokenResult = getMpAccessToken();
        System.out.println(accessTokenResult.toString());
    }
    
    private AccessTokenResult getMpAccessToken() {
        return wxService.getMpAccessToken("wx66eb4ba1e78feff0", "333225363f6789756a61f24010dd4291");
    }

}
