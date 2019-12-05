package com.wang.gmall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试Jwt加密后的内容
 * @author 微笑
 * @date 2019/12/2 19:53
 */
public class TestJwt {
    public static void main(String[] args) {
        String key="2019.gmall.wang";
        Map<String,Object> map=new HashMap<>();
        map.put("meberId","1");
        map.put("username","wang");
        String ip="127.0.0.1";
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String encode = JwtUtil.encode(key, map, ip + date);
        System.out.println(encode);
    }
}
