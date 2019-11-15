package com.wang.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;
import com.wang.gmall.manage.mapper.BaseAttrServiceMapper;
import com.wang.gmall.manage.mapper.SpuServiceMapper;
import com.wang.gmall.service.SpuSerivce;
import org.springframework.beans.factory.annotation.Autowired;

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
    BaseAttrServiceMapper spuAttrServiceMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo=new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);

        return spuServiceMapper.select(pmsProductInfo);
    }

    @Override
    public List<PmsBaseSaleAttr> getbaseSaleAttrList() {
        return  spuAttrServiceMapper.selectAll();
    }
}
