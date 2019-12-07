package com.wang.gmall.service;

import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.bean.UmsMemberReceiveAddress;

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

    UmsMember getUmsMemberById(String id);

    UmsMemberReceiveAddress getUmsMemberReceiveAddressById(String id);

    UmsMember login(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsCheck);
}
