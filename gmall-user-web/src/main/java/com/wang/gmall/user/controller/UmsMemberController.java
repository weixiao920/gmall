package com.wang.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/11 15:37
 */
@RestController
@RequestMapping(value = "/user")
public class UmsMemberController {

    /**
     * @Autowired 改为 Reference  dubbo的
     */
    @Reference
    UmsMemberService umsMemberService;

    /**
     * 获取所有用户信息
     * @return
     */
    @GetMapping("/getUser")
    public List<UmsMember> getUmsMember(){
        List<UmsMember> list=umsMemberService.getUmsMember();
        return list;
    }

    /**
     * 根据id查询用户
    */
    @GetMapping("/getUser/{id}")
    public UmsMember getUmsMemberById(@PathVariable(value = "id") String id){
        return umsMemberService.getUmsMemberById(id);
    }

    /**
     * 添加新用户
     * @param umsMember
     * @return
     */
    @RequestMapping("/addUser")
    public int addUmsMember(UmsMember umsMember){

        return umsMemberService.addUmsMember(umsMember);
    }

    /**
     * 更改用户
     * @param umsMember
     * @return
     */
    @RequestMapping("/updateUser")
    public int updateUmsMember(UmsMember umsMember){
        return umsMemberService.updateUmsMember(umsMember);
    }

    /**
     * 根据id删除用户
     *@param id
     * @return
     */
    @RequestMapping("/deleteUser")
    public int deleteUmsMember(String id){
        return umsMemberService.deleteUmsMember(id);
    }

    /**
     * 获取所有用户收获地址信息
     */
    @RequestMapping("/getUmsMemberReceiveAddress")
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(){
        return umsMemberService.getUmsMemberReceiveAddress();
    }

    /**
     * 根据id查询用户收获地址信息
     */
    @RequestMapping("/getUmsMemberReceiveAddressById/{id}")
    public UmsMemberReceiveAddress getUmsMemberReceiveAddressById(@PathVariable(value = "id") String id){
        return umsMemberService.getUmsMemberReceiveAddressById(id);
    }
    /**
     * 添加用户收获地址信息
     * @param umsMemberReceiveAddress
     * @return
     */
    @RequestMapping("/addUmsMemberReceiveAddress")
    public int addUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress){

        return umsMemberService.addUmsMemberReceiveAddress(umsMemberReceiveAddress);
    }

    /**
     * 更改用户收获地址信息
     * @param umsMemberReceiveAddress
     * @return
     */
    @RequestMapping("/updateUmsMemberReceiveAddress")
    public int updateUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress){
        return umsMemberService.updateUmsMemberReceiveAddress(umsMemberReceiveAddress);
    }

    /**
     * 根据id删除用户收获地址信息
     *@param id
     * @return
     */
    @RequestMapping("/deleteUmsMemberReceiveAddress")
    public int deleteUmsMemberReceiveAddress(String id){
        return umsMemberService.deleteUmsMemberReceiveAddress(id);
    }
}
