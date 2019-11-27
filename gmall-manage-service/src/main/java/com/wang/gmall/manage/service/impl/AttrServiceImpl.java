package com.wang.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wang.gmall.bean.PmsBaseAttrInfo;
import com.wang.gmall.bean.PmsBaseAttrValue;
import com.wang.gmall.manage.mapper.AttrInfoMapper;
import com.wang.gmall.manage.mapper.AttrValueMapper;
import com.wang.gmall.service.AttrService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author 微笑
 * @date 2019/11/14 9:00
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    AttrInfoMapper attrInfoMapper;
    @Autowired
    AttrValueMapper attrValueMapper;

    @Override
    /**
     * 获取平台属性
     */
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {

        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoMapper.select(pmsBaseAttrInfo);
        /**
         * 获取平台属性值
         */
        for (PmsBaseAttrInfo pmsBaseAttrInfo1 : pmsBaseAttrInfos) {
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo1.getId());
            List<PmsBaseAttrValue> baseAttrValues = attrValueMapper.select(pmsBaseAttrValue);
            pmsBaseAttrInfo1.setAttrValueList(baseAttrValues);
        }

        return pmsBaseAttrInfos;
    }

    @Override
    public int saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        String attrId = pmsBaseAttrInfo.getId();
        if (StringUtils.isEmpty(attrId)) {
            /**
             * attrId为null 插入
             * insert insertSelective
             * 是      否将null值插入数据库
             * 先保存属性，才有attrId
             */
            attrInfoMapper.insertSelective(pmsBaseAttrInfo);

            List<PmsBaseAttrValue> attrValues = pmsBaseAttrInfo.getAttrValueList();
            if (attrValues != null) {
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValues) {
                    pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                    attrValueMapper.insertSelective(pmsBaseAttrValue);
                }
            }
        } else {
            /**
             * attrId不为null 修改
             * 修改属性
             */
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
            attrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);
            /**
             * 修改属性值
             * 先删除属性值，再插入
             */
            PmsBaseAttrValue pmsBaseAttrValueDel = new PmsBaseAttrValue();
            pmsBaseAttrValueDel.setAttrId(pmsBaseAttrInfo.getId());
            attrValueMapper.delete(pmsBaseAttrValueDel);

            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValues) {
                attrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        }

        return 1;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        return attrValueMapper.select(pmsBaseAttrValue);
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet) {
        String valueIdStr = StringUtils.join(valueIdSet, ",");

        return  attrInfoMapper.selectAttrValueListByValueId(valueIdStr);
    }


}
