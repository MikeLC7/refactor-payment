package com.refactor.payment.rest;


import com.refactor.mall.common.msg.RestResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.request.ShareUrlFindVo;
import vo.response.ResShareUrlVo;

@RestController
@RequestMapping(value = "test")
public class WebChatJSSDKController {


    /**
     * Description: 获取分享要素
     *
     * @param:
     *
     * @author: MikeLC
     *
     * @date: 2018/7/3 下午 05:23
     */
    @RequestMapping(value = "/test")
    public RestResponse getJSSDKInfoBaseVersion(@RequestBody ShareUrlFindVo shareUrlFindVo) {
        //
        return RestResponse.success("test");
    }



}
