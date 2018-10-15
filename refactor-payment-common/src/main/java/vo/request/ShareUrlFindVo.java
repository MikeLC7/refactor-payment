package vo.request;

import lombok.Data;

/**
 * Project: RefactorMall
 *
 * File: WebchatUrlFindVo
 *
 * Description: 微信-链接-类-FindVo
 *
 * @author: MikeLC
 *
 * @date: 2018/6/26 上午 11:28
 *
 * Copyright ( c ) 2018
 *
 */
@Data
public class ShareUrlFindVo extends BaseWebchatVo {

    //原链接
    private String requestUrl;
    //分享缩略图地址
    private String shareLogo;
    //分享标题文案
    private String shareTitle;
    //分享描述文案
    private String shareDesc;
    //分享落地链接
    private String shareUrl;



}
