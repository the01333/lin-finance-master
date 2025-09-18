package com.puxinxiaolin.finance.biz.config;

import com.puxinxiaolin.finance.biz.dto.vo.GenerateMpRegCodeVo;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObjectConvertor {

    // --------------------------------   MemberRegServiceImpl   ------------------------------------

    /**
     * MpQrCodeCreateResult -> GenerateMpRegCodeVo
     *
     * @param request
     * @return
     */
    GenerateMpRegCodeVo toGenerateMpRegCodeResponse(MpQrCodeCreateResult request);


}
