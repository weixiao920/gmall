package com.wang.gmall.user.mapper;

import com.wang.gmall.bean.UmsMember;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 叶瑟
 */
public interface UmsMemberMapper extends Mapper<UmsMember> {

    List<UmsMember> selectUmsMember();
}
