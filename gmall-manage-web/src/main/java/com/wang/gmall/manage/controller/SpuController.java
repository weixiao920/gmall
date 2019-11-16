package com.wang.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.bean.PmsProductImage;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;
import com.wang.gmall.manage.util.PmsUploadUtil;
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
        /**
         * 将图片或音视频上传到分布式文件存储系统
         * 将图片的存储返回给也页面
         */
        String imgUrl= PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;

    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        String s = spuSerivce.saveSpuInfo(pmsProductInfo);
        return s;
    }

    /**
     * 获取销售属性
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
       return spuSerivce.spuSaleAttrList(spuId);
    }
    /**
     * 获取图片
     */
    @RequestMapping("spuImageList")
    public List<PmsProductImage> spuImageList(String spuId){
        return spuSerivce.spuImageList(spuId);
    }
}
