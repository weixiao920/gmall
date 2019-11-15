package com.wang.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wang.gmall.bean.PmsBaseCatalog1;
import com.wang.gmall.bean.PmsBaseCatalog2;
import com.wang.gmall.bean.PmsBaseCatalog3;
import com.wang.gmall.manage.mapper.Catlog2Mapper;
import com.wang.gmall.manage.mapper.Catlog3Mapper;
import com.wang.gmall.manage.mapper.CatlogMapper;
import com.wang.gmall.service.CatlogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/13 17:48
 */
@Service
public class CatlogServiceImpl implements CatlogService {

    @Autowired
    CatlogMapper catlogMapper;
    @Autowired
    Catlog2Mapper catlog2Mapper;
    @Autowired
    Catlog3Mapper catlog3Mapper;


    @Override
    public List<PmsBaseCatalog1> getCatlog1() {
        return catlogMapper.selectAll();
    }

    @Override
    public List<PmsBaseCatalog2> getCatlog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        return catlog2Mapper.select(pmsBaseCatalog2);
    }

    @Override
    public List<PmsBaseCatalog3> getCatlog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2_id(catalog2Id);
        return catlog3Mapper.select(pmsBaseCatalog3);
    }

}
