package com.wang.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.bean.PmsSkuSaleAttrValue;
import com.wang.gmall.service.PmsSkuInfoService;
import com.wang.gmall.service.SpuSerivce;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 微笑
 * @date 2019/11/16 16:01
 */
@Controller
public class ItemController {

    @Reference
    PmsSkuInfoService pmsSkuInfoService;

    @Reference
    SpuSerivce spuSerivce;
    /**
     * 获取skuInfo对象
     * @param skuId
     * @return
     */
    @RequestMapping("/item/{skuId}")
    public String item(@PathVariable(value = "skuId") String skuId, ModelMap modelMap){
        /**
         * sku对象
         */
        PmsSkuInfo pmsSkuInfo= pmsSkuInfoService.getSkuInfo(skuId);
        /**
         * 销售属性列表
         */
        List<PmsProductSaleAttr> productSaleAttrList=spuSerivce.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("skuInfo",pmsSkuInfo);
        modelMap.put("spuSaleAttrListCheckBySku",productSaleAttrList);
        /**
         * 查询当前的sku的spu的其他sku的集合hash表
         */
        Map<String,String> skuAttrHash=new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos= pmsSkuInfoService.getSkuAttrValueListBySpu(pmsSkuInfo.getProductId());
        for(PmsSkuInfo skuInfo:pmsSkuInfos){
            String value=skuInfo.getId();
            String key="";
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue:skuSaleAttrValueList){
                key+=pmsSkuSaleAttrValue.getSaleAttrValueId()+"|";
            }
            skuAttrHash.put(key,value);
        }

        /**
         * 将sku销售属性hash表放到页面
         */
        String skuAttrHashJsonStr = JSON.toJSONString(skuAttrHash);
        modelMap.put("skuAttrHashJsonStr",skuAttrHashJsonStr);
        return "item";
    }
}
