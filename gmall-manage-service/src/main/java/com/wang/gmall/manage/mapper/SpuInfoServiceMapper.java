package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/15 19:53
 */
public interface SpuInfoServiceMapper extends Mapper<PmsProductSaleAttr> {

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);
}
