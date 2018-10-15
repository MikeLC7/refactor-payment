package vo.response;

import lombok.Data;

/**
 * Project: RefactorMall
 *
 * File: ResShareUrlVo
 *
 * Description: 分享信息返回VO
 *
 * @author: MikeLC
 *
 * @date: 2018/6/26 上午 11:43
 *
 * Copyright ( c ) 2018
 *
 */
@Data
public class ResShareUrlVo {

    //appId
    private String appId;
    //nonceStr
    private String nonceStr;
    //timestamp
    private String timestamp;
    //原链接
    private String requestUrl;
    //延签
    private String signature;
    //jsapi_ticket
    private String jsapi_ticket;
    //分享缩略图地址
    private String shareLogo;
    //分享标题文案
    private String shareTitle;
    //分享描述文案
    private String shareDesc;
    //分享落地链接
    private String shareUrl;




}
