package org.xxpay.boot.service;

import com.alibaba.fastjson.JSONObject;
import org.xxpay.dal.dao.model.PayChannel;

import java.util.List;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
public interface IPayChannelService {

    Map selectPayChannel(String jsonParam);

    JSONObject getByMchIdAndChannelId(String mchId, String channelId);

    List<PayChannel> getEnablePayChannelListByMchId(String mchId);

    PayChannel selectPayChannelEntity(String jsonParam);

    PayChannel getEntityByMchIdAndChannelId(String mchId, String channelId);
}
