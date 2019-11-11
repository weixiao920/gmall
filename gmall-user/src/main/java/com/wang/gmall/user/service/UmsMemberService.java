package com.wang.gmall.user.service;

import com.wang.gmall.user.bean.UmsMember;
import com.wang.gmall.user.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberService {
    List<UmsMember> getUmsMember();

    int addUmsMember(UmsMember umsMember);

    int updateUmsMember(UmsMember umsMember);

    int deleteUmsMember(String id);

    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress();

    int addUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress);

    int updateUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress);

    int deleteUmsMemberReceiveAddress(String id);
}
