package com.refactor.payment.rest;

import com.refactor.mall.common.msg.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.common.util.MyLog;
import org.xxpay.dal.dao.model.PayChannel;

import java.util.List;

/**
 * Project: RefactorMall
 *
 * File: PayChannelController
 *
 * Description: 补充-基础信息查询
 *
 * @author: MikeLC
 *
 * @date: 2018/7/31 下午 05:20
 *
 * Copyright ( c ) 2018
 *
 */
@RestController
public class PayChannelController {

    private final MyLog _log = MyLog.getLog(PayChannelController.class);


    @Value("${defaultpay.mchId}")
    private String defaultPayMchId;
    // 支付渠道ID, WX_NATIVE(微信扫码),WX_JSAPI(微信公众号或微信小程序),WX_APP(微信APP),WX_MWEB(微信H5),ALIPAY_WAP(支付宝手机支付),ALIPAY_PC(支付宝网站支付),ALIPAY_MOBILE(支付宝移动支付)

    /**
     * Description:
     *
     * @param:
     *
     * @author: MikeLC
     *
     * @date: 2018/8/1 下午 05:43
     */
    @RequestMapping(value = "/get_channel_by_mach",method = RequestMethod.POST)
    public RestResponse<List<PayChannel>> payOrder(@RequestParam String mchId) {
        if (StringUtils.isBlank(mchId)){
            mchId = defaultPayMchId;
        }
        //List<PayChannel> payChannelList = this.payChannelService.getEnablePayChannelListByMchId(mchId);
        List<PayChannel> payChannelList = null;
        return RestResponse.success(payChannelList);
    }




}
