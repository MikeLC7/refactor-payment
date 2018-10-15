package vo.request;


import lombok.Data;

/**
 * Project: RefactorMall
 *
 * File: WebchatUrlFindVo
 *
 * Description: 微信-基础请求类
 *
 * @author: MikeLC
 *
 * @date: 2018/6/26 上午 11:28
 *
 * Copyright ( c ) 2018
 *
 */
@Data
public class BaseWebchatVo {

    //snsapi_base（不弹出授权页面，只能拿到用户openid）snsapi_userinfo（弹出授权页面，这个可以通过 openid 拿到昵称、性别、所在地）
    private Boolean snsapiBase = true;



}
