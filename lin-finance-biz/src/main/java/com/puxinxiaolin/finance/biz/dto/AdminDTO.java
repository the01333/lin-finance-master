package com.puxinxiaolin.finance.biz.dto;

import com.puxinxiaolin.common.dto.BaseUserInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class AdminDTO extends BaseUserInfoDTO {

    /**
     * 权限
     */
    private List<Integer> permissions;
    
}
