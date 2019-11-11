package com.wang.gmall.user.service.impl;

import com.wang.gmall.user.bean.UmsMember;
import com.wang.gmall.user.bean.UmsMemberReceiveAddress;
import com.wang.gmall.user.mapper.UmsMemberMapper;
import com.wang.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.wang.gmall.user.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/11 15:42
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    UmsMemberMapper umsMemberMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
    @Override
    public List<UmsMember> getUmsMember() {
        //List<UmsMember> umsMembers = umsMemberMapper.selectUmsMember();
        List<UmsMember> umsMembers = umsMemberMapper.selectAll();
        return umsMembers;
    }

    @Override
    public int addUmsMember(UmsMember umsMember) {
        return umsMemberMapper.insert(umsMember);
    }

    @Override
    public int updateUmsMember(UmsMember umsMember) {
        return umsMemberMapper.updateByPrimaryKey(umsMember);
    }

    @Override
    public int deleteUmsMember(String id) {
        return umsMemberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress() {
        return umsMemberReceiveAddressMapper.selectAll();
    }

    @Override
    public int addUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        return umsMemberReceiveAddressMapper.insert(umsMemberReceiveAddress);
    }

    @Override
    public int updateUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        return umsMemberReceiveAddressMapper.updateByPrimaryKey(umsMemberReceiveAddress);
    }

    @Override
    public int deleteUmsMemberReceiveAddress(String id) {
        return umsMemberReceiveAddressMapper.deleteByPrimaryKey(id);
    }

}
