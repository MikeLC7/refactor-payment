package com.refactor.payment.feign;

import com.refactor.mall.common.msg.RestResponse;
import com.refactor.mall.common.vo.busi.ucenter.response.ResMemberLoginVo;
import com.refactor.payment.feignFail.MemberFromWebchatFailImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Project: RefactorMall
 *
 * File: IMemberFromWebchatService
 *
 * Description:
 *
 * @author: MikeLC
 *
 * @date: 2018/6/26 下午 11:45
 *
 * Copyright ( c ) 2018
 *
 */
@FeignClient(value = "refactor-mall-ucenter",fallback = MemberFromWebchatFailImpl.class)
public interface IMemberFromWebchatService {

    @RequestMapping(value = "/api/memberWebchatInfo/rpc/getSynMemberFromWebChat", method = RequestMethod.POST, consumes = "application/json")
    RestResponse<ResMemberLoginVo> getSynMemberFromWebChat(@RequestParam(value = "userInfoStr") String userInfoStr);



}
