package com.wang.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.service.PmsSkuInfoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 微笑
 * @date 2019/11/15 22:08
 */
@RestController
@CrossOrigin
public class SkuController {

    @Reference
    PmsSkuInfoService pmsSkuInfoService;

    @RequestMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        pmsSkuInfoService.saveSkuInfo(pmsSkuInfo);

        return "success";
    }


}
