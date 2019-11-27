package com.wang.gmall.service;

import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.bean.PmsProductImage;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/14 20:24
 */
public interface SpuSerivce {
    List<PmsProductInfo> spuList(String catalog3Id);

    List<PmsBaseSaleAttr> getbaseSaleAttrList();

    String saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);
}
