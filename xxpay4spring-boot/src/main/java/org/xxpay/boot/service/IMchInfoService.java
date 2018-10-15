package org.xxpay.boot.service;

import com.alibaba.fastjson.JSONObject;
import org.xxpay.dal.dao.model.MchInfo;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
public interface IMchInfoService {

    Map selectMchInfo(String jsonParam);

    JSONObject getByMchId(String mchId);

    MchInfo selectMchInfoEntity(String jsonParam);

    MchInfo getEntityByMchId(String mchId);

}
