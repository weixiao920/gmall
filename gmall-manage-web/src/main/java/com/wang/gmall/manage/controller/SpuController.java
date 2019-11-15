package com.wang.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;
import com.wang.gmall.service.SpuSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/14 20:12
 */
@RestController
@CrossOrigin
public class SpuController {

    @Reference
    SpuSerivce spuSerivce;


    @RequestMapping("spuList")
    public List<PmsProductInfo> getspuList(String catalog3Id){
        return spuSerivce.spuList(catalog3Id);
    }

    /**
     * 获取销售属性
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    public List<PmsBaseSaleAttr> getbaseSaleAttrList(){
        return spuSerivce.getbaseSaleAttrList();
    }

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){

        String imgUrl="http://img4.imgtn.bdimg.com/it/u=2738275876,2435985502&fm=15&gp=0.jpg";
        return imgUrl;

    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        return "success";
    }
}
