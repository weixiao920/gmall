package com.wang.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wang.gmall.bean.*;
import com.wang.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.wang.gmall.manage.mapper.PmsSkuImageMapper;
import com.wang.gmall.manage.mapper.PmsSkuInfoMapper;
import com.wang.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.wang.gmall.service.PmsSkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/15 22:13
 */
@Service
/**
 * 保存SKU
 */
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfoMapper.insert(pmsSkuInfo);
        String productId=pmsSkuInfo.getProductId();

        /**
         * 保存图片
         */
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for(PmsSkuImage pmsSkuImage:skuImageList){
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        /**
         * 保存平台属性
         */
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for(PmsSkuAttrValue pmsSkuAttrValue:skuAttrValueList){
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }

        /**
         * 保存销售属性
         */
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue:skuSaleAttrValueList){
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
    }

    @Override
    public PmsSkuInfo getSkuInfo(String skuId) {
        PmsSkuInfo pmsSkuInfo=new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        /**
         * 获取图片集合
         */
        PmsSkuImage pmsSkuImage=new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);

        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);

        return skuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuAttrValueListBySpu(String id) {
        return pmsSkuInfoMapper.selectSkuAttrValueListBySpu(id);
    }


}
