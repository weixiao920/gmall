package com.wang.gmall.passport;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.HttpclientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class GmallPassportWebApplicationTests {

    @Test
    void contextLoads() {
        /**
         * App Key：525142805
         * App Secret：8a0d9adf6ca67703adc7a5ba74a3d8b5
         * "uid":"5718928847"
         * "access_token":"2.00rdCCPG0Pd8XZfc196f313dw86tlD"
         * 1请求授权地址，用户和第三方进行授权协议签订
         *
         */
        String s1 = "https://api.weibo.com/oauth2/authorize?client_id=525142805&response_type=code&redirect_uri=http://passport.gmall.com:8086/vLogin";
        //2 通过回调地址获得授权码 code
        String s2 = "020c0f56908ce5d1ac0d67d670d2641a";

    }

    @Test
    void getAccessToken() {
        String s3 = "https://api.weibo.com/oauth2/access_token?";
        //?client_id=525142805&client_secret=8a0d9adf6ca67703adc7a5ba74a3d8b5&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8086/vlogin&code=CODE";
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "525142805");
        map.put("client_secret", "8a0d9adf6ca67703adc7a5ba74a3d8b5");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://passport.gmall.com:8086/vlogin");
        map.put("code", "abfcd532a54a0a177ccb235cda3f23ba");
        String accessTokenJson = HttpclientUtil.doPost(s3, map);

        Map accessTokenMap = JSON.parseObject(accessTokenJson, Map.class);
        accessTokenMap.get("access_token");
        accessTokenMap.get("uid");
        System.out.println(accessTokenJson);
    }

    @Test
    void getUserInfo() {
        // 4 用access_token查询用户信息
//        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00rdCCPG0Pd8XZfc196f313dw86tlD&uid=5718928847";
//        String userJson = HttpclientUtil.doGet(s4);
//        Map<String,String> userMap = JSON.parseObject(userJson, Map.class);
//        System.out.println(userMap.get("1"));
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00rdCCPG0Pd8XZfc196f313dw86tlD&uid=5718928847";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);
        System.out.println(user_map.get("screen_name"));//获取用户名

    }
}
