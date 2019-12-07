package com.wang.gmall.service;

import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/15 22:13
 */
public interface PmsSkuInfoService {

    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuInfo(String skuId);

    List<PmsSkuInfo> getSkuAttrValueListBySpu(String id);

    List<PmsSkuInfo> getSkuAll();

    boolean checkPrice(String productSkuId, BigDecimal price);
}
