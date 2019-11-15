package com.wang.gmall.service;

import com.wang.gmall.bean.PmsBaseCatalog1;
import com.wang.gmall.bean.PmsBaseCatalog2;
import com.wang.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/13 17:44
 */
public interface CatlogService {
    List<PmsBaseCatalog1> getCatlog1() ;

    List<PmsBaseCatalog2> getCatlog2(String catalog2);

    List<PmsBaseCatalog3> getCatlog3(String catalog2Id);
}
