package com.puxinxiaolin.finance.biz.service.impl;

import com.puxinxiaolin.common.constant.CommonConstant;
import com.puxinxiaolin.finance.biz.domain.Member;
import com.puxinxiaolin.finance.biz.mapper.MemberMapper;
import com.puxinxiaolin.finance.biz.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    final MemberMapper memberMapper;

    /**
     * 用户注册
     *
     * @param tenantId
     * @return
     */
    @Override
    public Long reg(Long tenantId) {
        Member member = new Member();
        member.setTenantId(tenantId);
        // TODO [YCcLin 2025/9/14]: 先统一分配管理员 
        member.setSysRoleIds("[" + CommonConstant.ROLE_ADMIN + "]");
        member.initDefault();

        memberMapper.insert(member);
        return member.getId();
    }

    /**
     * 根据 id 获取用户信息
     *
     * @param memberId
     * @return
     */
    @Override
    public Member get(Long memberId) {
        return null;
    }

    /**
     * 根据 id 集合获取用户信息
     *
     * @param memberIds
     * @return
     */
    @Override
    public List<Member> listByIds(Set<Long> memberIds) {
        return Collections.emptyList();
    }
}
