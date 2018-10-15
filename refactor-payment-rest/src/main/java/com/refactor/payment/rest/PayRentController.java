package com.refactor.payment.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.refactor.mall.common.enums.ResponseCode;
import com.refactor.mall.common.exception.BaseException;
import com.refactor.mall.common.msg.RestResponse;
import com.refactor.mall.common.util.compute.MathUtil;
import com.refactor.mall.common.vo.busi.payment.request.RentOrderPaymentRequestVo;
import com.refactor.mall.common.vo.busi.rent.response.ResRentOrderVo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.common.util.*;
import util.OAuth2RequestParamHelper;
import util.vx.WxApi;
import util.vx.WxApiClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 支付订单,包括:统一下单,订单查询,补单等接口
 * @author dingzhiwei jmdhappy@126.com
 * @date 2017-07-05
 * @version V1.0
 * @Copyright: www.xxpay.org
 */
@RestController()
@RequestMapping("rent")
public class PayRentController {

    private final MyLog _log = MyLog.getLog(PayRentController.class);
    @Autowired
    private RedisTemplate redisTemplate;
    //
    private AtomicLong seq = new AtomicLong(0L);
    //@Autowired
    //private IRentOrderService iRentOrderService;
    @Value("${defaultpay.mchId}")
    private String defaultPayMchId;
    @Value("${defaultpay.channelid}")
    private String defaultPayChannelId;
    //
    private String mchId;
    // 加签key
    @Value("${defaultpay.reqKey}")
    private String reqKey;
    // 验签key
    @Value("${defaultpay.resKey}")
    private String resKey;
    //
    private String AppID;
    //
    private String AppSecret;
    //
    @Value("${defaultpay.baseUrl}")
    private String baseUrl;
    static final String notifyUrl =  "/goods/payNotify";
    private final static String QR_PAY_URL = "/goods/qrPay.html";
    private final static String GetOpenIdURL = "/goods/getOpenId";
    private final static String GetOpenIdURL2 = "/goods/getOpenId2";


    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> pay(@RequestBody RentOrderPaymentRequestVo rentOrderPaymentRequestVo) {
        //
        Map<String, Object> params = new HashedMap();
        params.put("productId", 123321);
        //
        Map retMap = this.createPayOrder(rentOrderPaymentRequestVo, params);
        //
        return RestResponse.success(ResponseCode.SUCCESS.getName(), retMap.toString());

    }

    private Map createPayOrder(RentOrderPaymentRequestVo rentOrderPaymentRequestVo, Map<String, Object> params) {
        //BaseInfo
        String mchId = rentOrderPaymentRequestVo.getMchId(); //支付账号ID
        String payChannelId = rentOrderPaymentRequestVo.getPayChannelId(); //支付渠道ID
        BigDecimal moneyTopay = MathUtil.mul(rentOrderPaymentRequestVo.getMoneyToPay(), new BigDecimal(100));
        String payType = rentOrderPaymentRequestVo.getPayType(); //支付内容类型
        //
        if (StringUtils.isBlank(mchId)){mchId = defaultPayMchId;}
        if (StringUtils.isBlank(payChannelId)){payChannelId = defaultPayChannelId;}
        //RentOrderInfo
        String rentOrderId = rentOrderPaymentRequestVo.getRentOrderId() + ""; //租赁订单ID
          /*   if (rentOrderId == null){
            throw BaseException.message("租赁订单ID为空！");
        }*/
        //TODO Test
        rentOrderId = String.format("%s%s%06d", "G", DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
        ResRentOrderVo resRentOrderVo = new ResRentOrderVo();
        resRentOrderVo.setId(100001l);
        resRentOrderVo.setOrderSn("testOrderSn");
        resRentOrderVo.setProductName("测试名称");
        resRentOrderVo.setProductName2("测试名称2");
        //
        /*RentOrderFindVo rentOrderFindVo = new RentOrderFindVo();
        rentOrderFindVo.setId(rentOrderId);
        ResRentOrderVo resRentOrderVo = this.iRentOrderService.getRentOrderInfo(rentOrderFindVo).getData();
        if (resRentOrderVo == null){
            throw BaseException.message("租赁订单：" + ResponseCode.DATA_LOST.getName());
        }
        if (!resRentOrderVo.getNstatus().equals(CommonNstatus.ENABLE.getCode())){
            throw BaseException.message("租赁订单：" + ResponseCode.NSTATUS_DISABLE.getName());
        }
        //校验
        if (!resRentOrderVo.getOrderState().equals(RentOrderStateEnum.FOR_PAY.getCode())){
            throw BaseException.message("租赁订单：" + ResponseCode.ALREADY_PAID.getName());
        }*/
        //获取商户信息
       /* MchInfo mchInfo = this.mchInfoService.getEntityByMchId(mchId);
        if (mchInfo == null){
            throw BaseException.message("商户信息丢失");
        }
        reqKey = mchInfo.getReqKey();
        resKey = mchInfo.getResKey();*/
        //
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", rentOrderId);           // 商户订单号
        paramMap.put("channelId", payChannelId);             // 支付渠道ID, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", moneyTopay.intValue());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", rentOrderPaymentRequestVo.getOperateIp());        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", resRentOrderVo.getProductName());
        paramMap.put("body", resRentOrderVo.getProductName2());
        paramMap.put("notifyUrl", baseUrl + notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2
        JSONObject extra = new JSONObject();
        if (params != null){
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                extra.put(entry.getKey(), entry.getValue());
            }
            //extra.put("openId", params.get("openId"));
        }
        extra.put("rentOrderId", resRentOrderVo.getOrderSn());
        paramMap.put("extra", extra.toJSONString());  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, reqKey);
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = baseUrl + "/pay/create_order?";
        String result = XXPayUtil.call4Post(url + reqData);
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            }else {
                System.err.println("=========支付中心下单验签失败=========");
                throw BaseException.message("支付中心下单验签失败");
            }
        } else {
            throw BaseException.message(retMap.get("retMsg")+"");
        }
        //
        return retMap;
    }

    @RequestMapping("/openQrPay.html")
    public String openQrPay(ModelMap model) {
        return "openQrPay";
    }

    @RequestMapping("/qrPay.html")
    public String qrPay(ModelMap model, HttpServletRequest request, Long amount) {
        String logPrefix = "【二维码扫码支付】";
        String view = "qrPay";
        _log.info("====== 开始接收二维码扫码支付请求 ======");
        String ua = request.getHeader("User-Agent");
        String goodsId = "G_0001";
        _log.info("{}接收参数:goodsId={},amount={},ua={}", logPrefix, goodsId, amount, ua);
        String client = "alipay";
        String channelId = "ALIPAY_WAP";
        if(StringUtils.isBlank(ua)) {
            String errorMessage = "User-Agent为空！";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            return view;
        }else {
            if(ua.contains("Alipay")) {
                client = "alipay";
                channelId = "ALIPAY_WAP";
            }else if(ua.contains("MicroMessenger")) {
                client = "wx";
                channelId = "WX_JSAPI";
            }
        }
        if(client == null) {
            String errorMessage = "请用微信或支付宝扫码";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            return view;
        }
        // 先插入订单数据
        RentOrderPaymentRequestVo rentOrderPaymentRequestVo = new RentOrderPaymentRequestVo();
        Map<String, String> orderMap = null;
        if ("alipay".equals(client)) {
            _log.info("{}{}扫码下单", logPrefix, "支付宝");
            Map params = new HashMap<>();
            params.put("channelId", channelId);
            // 下单
            orderMap = createPayOrder(rentOrderPaymentRequestVo, params);
        }else if("wx".equals(client)){
            _log.info("{}{}扫码", logPrefix, "微信");
            // 判断是否拿到openid，如果没有则去获取
            String openId = request.getParameter("openId");
            if (StringUtils.isNotBlank(openId)) {
                _log.info("{}openId：{}", logPrefix, openId);
                Map params = new HashMap<>();
                params.put("channelId", channelId);
                params.put("openId", openId);
                // 下单
                orderMap = createPayOrder(rentOrderPaymentRequestVo, params);
            }else {
                String redirectUrl = baseUrl + QR_PAY_URL + "?amount=" + amount;
                String url = baseUrl + GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
                _log.info("跳转URL={}", url);
                return "redirect:" + url;
            }
        }
        model.put("rentOrderPaymentRequestVo", rentOrderPaymentRequestVo);
        model.put("amount", AmountUtil.convertCent2Dollar(amount+""));
        if(orderMap != null) {
            model.put("orderMap", orderMap);
        }
        model.put("client", client);
        return view;
    }

    /**
     * 获取code
     * @return
     */
    @RequestMapping("/getOpenId")
    public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String openId = "";
        //getPayChannelInfo
        String mchId = request.getParameter("mchId"); //支付账号ID
        if (StringUtils.isBlank(mchId)){mchId = defaultPayMchId;}
        //TODO ADD
        /*PayChannel payChannel = this.payChannelService.getEntityByMchIdAndChannelId(mchId, PayConstant.PAY_CHANNEL_WX_JSAPI);
        if (payChannel == null){
            throw BaseException.message("商户支付渠道信息丢失");
        }
        AppID = JsonUtil.getJSONObjectFromObj(payChannel.getParam()).get("appId") + "";
        AppSecret = JsonUtil.getJSONObjectFromObj(payChannel.getParam()).get("key") + "";*/
        //
        if(!StringUtils.isBlank(code)){//如果request中包括code，则是微信回调
            try {
                openId = WxApiClient.getOAuthOpenId(AppID, AppSecret, code);
                _log.info("调用微信返回openId={}", openId);
            } catch (Exception e) {
                _log.error(e, "调用微信查询openId异常");
            }
            if(redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&openId=" + openId;
            }else {
                redirectUrl += "?openId=" + openId;
            }
            response.sendRedirect(redirectUrl);
        }else{//oauth获取code
            String redirectUrl4Vx = baseUrl + GetOpenIdURL + "?redirectUrl=" + redirectUrl;
            String state = OAuth2RequestParamHelper.prepareState(request);
            String url = WxApi.getOAuthCodeUrl(AppID, redirectUrl4Vx, "snsapi_base", state);
            _log.info("跳转URL={}", url);
            response.sendRedirect(url);
        }
    }

    /**
     * 获取code
     * @return
     */
    @RequestMapping("/getOpenId2")
    public void getOpenId2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String openId = "";
        //getPayChannelInfo
        String mchId = request.getParameter("mchId"); //支付账号ID
        if (StringUtils.isBlank(mchId)){mchId = defaultPayMchId;}
        //TODO ADD
        /*PayChannel payChannel = this.payChannelService.getEntityByMchIdAndChannelId(mchId, PayConstant.PAY_CHANNEL_WX_JSAPI);
        if (payChannel == null){
            throw BaseException.message("商户支付渠道信息丢失");
        }
        AppID = JsonUtil.getJSONObjectFromObj(payChannel.getParam()).get("appId") + "";
        AppSecret = JsonUtil.getJSONObjectFromObj(payChannel.getParam()).get("key") + "";*/
        //
        if(!StringUtils.isBlank(code)){//如果request中包括code，则是微信回调
            try {
                openId = WxApiClient.getOAuthOpenId(AppID, AppSecret, code);
                _log.info("调用微信返回openId={}", openId);
            } catch (Exception e) {
                _log.error(e, "调用微信查询openId异常");
            }
            if(redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&openId=" + openId;
            }else {
                redirectUrl += "?openId=" + openId;
            }
            response.sendRedirect(redirectUrl);
        }else{//oauth获取code
            //http://www.abc.com/xxx/get-weixin-code.html?appid=XXXX&scope=snsapi_base&state=hello-world&redirect_uri=http%3A%2F%2Fwww.xyz.com%2Fhello-world.html
            String redirectUrl4Vx = baseUrl + GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
            String url = String.format("http://www.xiaoshuding.com/get-weixin-code.html?appid=%s&scope=snsapi_base&state=hello-world&redirect_uri=%s", AppID, WxApi.urlEnodeUTF8(redirectUrl4Vx));
            _log.info("跳转URL={}", url);
            response.sendRedirect(url);
        }
    }

    /**
     * 接收支付中心通知
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/payNotify")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("====== 开始处理支付中心通知 ======");
        Map<String,Object> paramMap = request2payResponseMap(request, new String[]{
                "payOrderId","mchId","mchOrderNo","channelId","amount","currency","status", "clientIp",
                "device",  "subject", "channelOrderNo", "param1",
                "param2","paySuccTime","backType","sign"
        });
        _log.info("支付中心通知请求参数,paramMap={}", paramMap);
        if (!verifyPayResponse(paramMap)) {
            String errorMessage = "verify request param failed.";
            _log.warn(errorMessage);
            outResult(response, "fail");
            return;
        }
        String payOrderId = (String) paramMap.get("payOrderId");
        String mchOrderNo = (String) paramMap.get("mchOrderNo");
        String resStr;
        try {

            // 执行业务逻辑
            int ret = 1;
            // ret返回结果
            // 等于1表示处理成功,返回支付中心success
            // 其他值,返回支付中心fail,让稍后再通知
            if(ret == 1) {
                resStr = "success";
            }else {
                resStr = "fail";
            }
        }catch (Exception e) {
            resStr = "fail";
            _log.error(e, "执行业务异常,payOrderId=%s.mchOrderNo=%s", payOrderId, mchOrderNo);
        }
        _log.info("响应支付中心通知结果:{},payOrderId={},mchOrderNo={}", resStr, payOrderId, mchOrderNo);
        outResult(response, resStr);
        _log.info("====== 支付中心通知处理完成 ======");
    }

    @RequestMapping("/notify_test")
    public void notifyTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        outResult(response, "success");
    }

    @RequestMapping("/toAliPay.html")
    @ResponseBody
    public String toAliPay(RentOrderPaymentRequestVo rentOrderPaymentRequestVo, Map<String, Object> params) {
        String logPrefix = "【支付宝支付】";
        _log.info("====== 开始接收支付宝支付请求 ======");
        String channelId = rentOrderPaymentRequestVo.getPayChannelId();
        _log.info("{}接收参数:goodsId={},amount={},channelId={}", logPrefix, null, rentOrderPaymentRequestVo.getMoneyToPay(), channelId);
        // 下单
        Map<String, String> orderMap = createPayOrder(rentOrderPaymentRequestVo, null);
        if(orderMap != null && "success".equalsIgnoreCase(orderMap.get("resCode"))) {

            _log.info("修改商品订单,返回:{}");
        }
        if(PayConstant.PAY_CHANNEL_ALIPAY_MOBILE.equalsIgnoreCase(channelId)) return orderMap.get("payParams");
        return orderMap.get("payUrl");
    }

    void outResult(HttpServletResponse response, String content) {
        response.setContentType("text/html");
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.print(content);
            _log.error("response xxpay complete.");
        } catch (IOException e) {
            _log.error(e, "response xxpay write exception.");
        }
    }

    public Map<String, Object> request2payResponseMap(HttpServletRequest request, String[] paramArray) {
        Map<String, Object> responseMap = new HashMap<>();
        for (int i = 0;i < paramArray.length; i++) {
            String key = paramArray[i];
            String v = request.getParameter(key);
            if (v != null) {
                responseMap.put(key, v);
            }
        }
        return responseMap;
    }

    public boolean verifyPayResponse(Map<String,Object> map) {
        String mchId = (String) map.get("mchId");
        String payOrderId = (String) map.get("payOrderId");
        String mchOrderNo = (String) map.get("mchOrderNo");
        String amount = (String) map.get("amount");
        String sign = (String) map.get("sign");

        if (StringUtils.isEmpty(mchId)) {
            _log.warn("Params error. mchId={}", mchId);
            return false;
        }
        if (StringUtils.isEmpty(payOrderId)) {
            _log.warn("Params error. payOrderId={}", payOrderId);
            return false;
        }
        if (StringUtils.isEmpty(amount) || !NumberUtils.isNumber(amount)) {
            _log.warn("Params error. amount={}", amount);
            return false;
        }
        if (StringUtils.isEmpty(sign)) {
            _log.warn("Params error. sign={}", sign);
            return false;
        }

        // 验证签名
        if (!verifySign(map)) {
            _log.warn("verify params sign failed. payOrderId={}", payOrderId);
            return false;
        }

        // 根据payOrderId查询业务订单,验证订单是否存在

        // 核对金额

        return true;
    }

    public boolean verifySign(Map<String, Object> map) {
        String mchId = (String) map.get("mchId");
        if(!this.mchId.equals(mchId)) return false;
        String localSign = PayDigestUtil.getSign(map, resKey, "sign");
        String sign = (String) map.get("sign");
        return localSign.equalsIgnoreCase(sign);
    }



}
