package com.wang.gmall.user.mapper;

import com.wang.gmall.user.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 叶瑟
 */

public interface UmsMemberMapper extends Mapper<UmsMember> {

    List<UmsMember> selectUmsMember();
}
