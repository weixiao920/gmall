package com.wang.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.bean.PmsBaseCatalog1;
import com.wang.gmall.bean.PmsBaseCatalog2;
import com.wang.gmall.bean.PmsBaseCatalog3;
import com.wang.gmall.service.CatlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 叶瑟
 */
@CrossOrigin
@RestController

public class CatlogController {

    @Reference
    CatlogService catlogService;


    @RequestMapping("getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1()
    {
        return catlogService.getCatlog1();
    }

    @RequestMapping("getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id)
    {
        return catlogService.getCatlog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id)
    {
        return catlogService.getCatlog3(catalog2Id);
    }
}
