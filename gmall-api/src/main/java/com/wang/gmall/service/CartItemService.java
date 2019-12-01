package com.wang.gmall.service;

import com.wang.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/27 16:06
 */
public interface CartItemService {
    OmsCartItem ifCartExitByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDB);

    void flushCartCache(String memberId);

    List<OmsCartItem> selectCartList(String userId);

    void checkCart(OmsCartItem omsCartItem);
}
