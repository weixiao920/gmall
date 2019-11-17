package com.wang.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wang.gmall.bean.*;
import com.wang.gmall.manage.mapper.*;
import com.wang.gmall.service.SpuSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/14 20:24
 */
@Service
public class SpuServiceImpl implements SpuSerivce {

    @Autowired
    SpuServiceMapper spuServiceMapper;

    @Autowired
    BaseAttrServiceMapper baseAttrServiceMapper;

    @Autowired
    SpuImageServiceMapper spuImageServiceMapper;

    @Autowired
    SpuInfoServiceMapper spuInfoServiceMapper;

    @Autowired
    SpuInfoValueServiceMapper spuInfoValueServiceMapper;



    @Override
    /**
     * 获取spu属性
     */
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo=new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);

        return spuServiceMapper.select(pmsProductInfo);
    }

    @Override
    public List<PmsBaseSaleAttr> getbaseSaleAttrList() {
        return  baseAttrServiceMapper.selectAll();
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        spuServiceMapper.insert(pmsProductInfo);

        /**
         * 存储图片
         */
        List<PmsProductImage> pmsProductImages=pmsProductInfo.getSpuImageList();
        if(pmsProductImages!=null){
            for (PmsProductImage pmsProductImage:pmsProductImages){
                /**
                 * 设置图片对应手机的id
                 */
                pmsProductImage.setProductId(pmsProductInfo.getId());
                    spuImageServiceMapper.insert(pmsProductImage);
            }
        }

        /**
         * 存储销售属性
         */
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null){
            for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList){
                /**
                 * 设置属性对应手机的id
                 */
                pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
                spuInfoServiceMapper.insert(pmsProductSaleAttr);
                /**
                 * 存储销售属性的值
                 */
                List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
                if(spuSaleAttrValueList!=null){
                    for(PmsProductSaleAttrValue pmsProductSaleAttrValue:spuSaleAttrValueList){
                        /**
                         * 设置属性值对应手机的id
                         */
                        pmsProductSaleAttrValue.setProductId(pmsProductInfo.getId());
                        spuInfoValueServiceMapper.insert(pmsProductSaleAttrValue);
                    }

                }
            }
        }
        return "success";
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr=new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        /**
         * 根据商品id获取商品销售属性
         */
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuInfoServiceMapper.select(pmsProductSaleAttr);
        for(PmsProductSaleAttr pmsProductSaleAttr1:pmsProductSaleAttrs){
            PmsProductSaleAttrValue pmsProductSaleAttrValue=new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(pmsProductSaleAttr1.getSaleAttrId());
            /**
             * 根据商品id 和 商品属性id 获取商品销售属性的值
             */
            List<PmsProductSaleAttrValue> productSaleAttrValues = spuInfoValueServiceMapper.select(pmsProductSaleAttrValue);
            pmsProductSaleAttr1.setSpuSaleAttrValueList(productSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    @Override
    /**
     * 获取图片
     */
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage=new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = spuImageServiceMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {

        return spuInfoServiceMapper.spuSaleAttrListCheckBySku(productId,skuId);
    }
}
