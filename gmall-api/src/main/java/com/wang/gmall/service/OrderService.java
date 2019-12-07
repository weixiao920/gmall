package com.wang.gmall.service;

import com.wang.gmall.bean.OmsOrder;

/**
 * @author 微笑
 * @date 2019/12/6 15:03
 */
public interface OrderService {
    String genTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    void saveOrder(OmsOrder omsOrder);
}
