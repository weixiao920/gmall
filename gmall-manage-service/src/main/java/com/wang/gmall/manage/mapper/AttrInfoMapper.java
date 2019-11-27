package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/14 9:00
 */
public interface AttrInfoMapper extends Mapper<PmsBaseAttrInfo> {

    int saveAttrValue(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrInfo> selectAttrValueListByValueId(@Param("valueIdStr") String valueIdStr);
}
