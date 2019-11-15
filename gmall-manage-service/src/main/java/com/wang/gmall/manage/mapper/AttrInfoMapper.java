package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsBaseAttrInfo;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author 微笑
 * @date 2019/11/14 9:00
 */
public interface AttrInfoMapper extends Mapper<PmsBaseAttrInfo> {

    int saveAttrValue(PmsBaseAttrInfo pmsBaseAttrInfo);
}
