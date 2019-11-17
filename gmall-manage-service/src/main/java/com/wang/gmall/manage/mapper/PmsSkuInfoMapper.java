package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsSkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/15 22:15
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectSkuAttrValueListBySpu(String id);
}
