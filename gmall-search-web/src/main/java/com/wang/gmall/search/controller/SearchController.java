package com.wang.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.bean.*;
import com.wang.gmall.service.AttrService;
import com.wang.gmall.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author 微笑
 * @date 2019/11/25 20:46
 */
@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    /**
     * 首页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){
        List<PmsSearchSkuInfo> list = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList",list);

        /**
         * 将平台属性id放入set集合
         */
        Set<String> valueIdSet=new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : list) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }
        /**
         * 根据平台属性id将平台属性查询出来
         */
        List<PmsBaseAttrInfo> pmsBaseAttrInfos=attrService.getAttrValueListByValueId(valueIdSet);
        modelMap.put("attrList",pmsBaseAttrInfos);

        /**
         * 对平台属性集合进一步处理，去掉当前条件中valueId所在的属性组
         */
        String[] delValueIds = pmsSearchParam.getValueId();
        /**
         * 面包屑集合
         */
        List<PmsSearchCrumb> pmsSearchCrumbs=new ArrayList<>();
        if(delValueIds!=null){
            for (String delValueId : delValueIds) {
                /**
                 * 迭代平台属性集合
                 */
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();

                PmsSearchCrumb pmsSearchCrumb=new PmsSearchCrumb();
                /**
                 * 设置面包屑属性
                 */
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamCrumb(pmsSearchParam,delValueId));

                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                        String attrValueId = pmsBaseAttrValue.getId();
                        /**
                         * 如果平台属行id==传进来的id,则将传进来的id所属于在平台属性组删除
                         */
                        if (delValueId.equals(attrValueId)) {
                            /**
                             * 设置面包屑name属性
                             */
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            iterator.remove();
                        }

                    }
                }
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList",pmsSearchCrumbs);
        }
        /**
         * 拼接url
         */
        String urlParam=getUrlParam(pmsSearchParam);
        modelMap.put("urlParam",urlParam);
        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            modelMap.put("keyword",keyword);
        }

        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String urlParam="";
        String[] valueId = pmsSearchParam.getValueId();
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            urlParam+="catalog3Id="+catalog3Id;
        }
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(keyword)){
                urlParam+="&";
            }
            urlParam+="keyword="+keyword;
        }

        if(valueId!=null){
            for (String pmsSkuAttrValue : valueId) {
                urlParam+="&valueId="+pmsSkuAttrValue;
            }
        }
        return urlParam;
    }

    private String getUrlParamCrumb(PmsSearchParam pmsSearchParam,String delValueId) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String urlParam="";
        String[] valueId = pmsSearchParam.getValueId();
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            urlParam+="catalog3Id="+catalog3Id;
        }
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(keyword)){
                urlParam+="&";
            }
            urlParam+="keyword="+keyword;
        }

        if(valueId!=null){
            for (String pmsSkuAttrValue : valueId) {
                if(!pmsSkuAttrValue.equals(delValueId)) {
                    urlParam += "&valueId=" + pmsSkuAttrValue;
                }
            }
        }
        return urlParam;
    }
}
