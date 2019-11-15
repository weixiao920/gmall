package com.wang.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.bean.PmsBaseAttrInfo;
import com.wang.gmall.bean.PmsBaseAttrValue;
import com.wang.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/14 8:49
 */
@CrossOrigin
@RestController
public class AttrController {

    @Reference
    AttrService attrService;

    @RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        return attrService.getAttrInfoList(catalog3Id);
    }

    @RequestMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        int save=attrService.saveAttrInfo(pmsBaseAttrInfo);

        return "success";
    }

    @RequestMapping("getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        return attrService.getAttrValueList(attrId);
    }

}
