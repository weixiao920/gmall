package com.wang.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.HttpclientUtil;
import com.wang.gmall.annotations.LoginRequired;
import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.service.UmsMemberService;
import com.wang.gmall.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Soundbank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 微笑
 * @date 2019/12/1 20:38
 */
@Controller
public class PassPortController {

    @Reference
    UmsMemberService umsMemberService;

    /**
     * 第三方登录
     *
     * @param code
     * @param request
     * @return
     */
    @RequestMapping("vLogin")
    public String vLogin(String code, HttpServletRequest request) {

        /**
         * 通过客户端返回的code获取access_token
         */
        String accessTokenUrl = "https://api.weibo.com/oauth2/access_token?";
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "525142805");
        map.put("client_secret", "8a0d9adf6ca67703adc7a5ba74a3d8b5");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://passport.gmall.com:8086/vLogin");
        map.put("code", code);
        String accessTokenJson = HttpclientUtil.doPost(accessTokenUrl, map);

        Map<String, String> accessTokenMap = JSON.parseObject(accessTokenJson, Map.class);
        String access_token = accessTokenMap.get("access_token");
        String access_id = accessTokenMap.get("uid");
        /**
         * 通过第三方平台给的access_token获取第三方平台的用户信息
         */
        String userInfoUrl = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + access_id;
        String user_json = HttpclientUtil.doGet(userInfoUrl);
        Map<String, String> user_map = JSON.parseObject(user_json, Map.class);
        String genders = user_map.get("gender");
        String gender = genders == "m" ? "0" : "1";
        /**
         * 将第三方平台用户信息写入数据库
         */
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setSourceUid(user_map.get("idstr"));
        umsMember.setNickname(user_map.get("screen_name"));
        umsMember.setGender(gender);
        umsMember.setCity(user_map.get("location"));
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);

        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberCheck = umsMemberService.checkOauthUser(umsCheck);
        if (umsMemberCheck == null) {
            umsMemberService.addUmsMember(umsMember);
        } else {
            umsMember = umsMemberCheck;
        }
        /**
         * 设置本网站的通行码token
         */
        String token = makeToken(umsMember, request);
        return "redirect:http://search.gmall.com:8084/index?token="+token;

    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {
        /**
         * 通过jwt校验token真假
         *
         */
        Map<String, String> map = new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token,"2019gmall0105", currentIp);

        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
        } else {
            map.put("status", "success");
        }


        return JSON.toJSONString(map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        /**
         * 调用用户服务验证用户名和密码
         */
        String token = "";
        UmsMember umsMemberLogin = umsMemberService.login(umsMember);
        if (umsMemberLogin != null) {
            /**
             * 验证成功，设置通行码token
             */
            Map<String, Object> map = new HashMap<>();
            map.put("umsMemberId", umsMemberLogin.getId());
            map.put("nickname", umsMemberLogin.getNickname());
            /**
             * 如果有nginx负载均衡，需要先在nginx设置 X-real-ip
             */
            String ip = request.getHeader("X-real-ip");
            if (StringUtils.isBlank(ip)) {
                /**
                 * 如果为空，说明没有设置nginx,或者nginx不可用
                 */
                ip = request.getRemoteAddr();
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            token = JwtUtil.encode("2019wang", map, ip);
        } else {
            /**
             * 验证失败
             */
            token = "fail";
        }
        return token;
    }

    @RequestMapping("index")
    public String index(String originUrl, ModelMap modelMap) {
        modelMap.put("originUrl", originUrl);
        return "index";
    }

    public String makeToken(UmsMember umsMember, HttpServletRequest request) {
        String token = null;
        Map<String, Object> map = new HashMap<>();
        map.put("umsMemberId", umsMember.getId());
        map.put("nickname", umsMember.getNickname());
        /**
         * 如果有nginx负载均衡，需要先在nginx设置 X-real-ip
         */
        String ip = request.getHeader("X-real-ip");
        if (StringUtils.isBlank(ip)) {
            /**
             * 如果为空，说明没有设置nginx,或者nginx不可用
             */
            ip = request.getRemoteAddr();
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
        }
        token = JwtUtil.encode("2019wang", map, ip);
        return token;
    }
}
