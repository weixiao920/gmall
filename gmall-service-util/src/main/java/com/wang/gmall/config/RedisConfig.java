package com.wang.gmall.config;


import com.wang.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 微笑
 * @date 2019/11/18 20:17
 *
 * spring整合redis的配置类
 */
@Configuration
public class RedisConfig {
    /**
     * 读取配置文件中的redis的ip地址
     */
    @Value("${spring.redis.host:disabled}")
    private String host = "192.168.222.20";
    @Value("${spring.redis.port:0}")
    private int port = 6179;
    @Value("${spring.redis.database:0}")
    private int database;
    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil=new RedisUtil();
        redisUtil.initPool(host,port,database);
        return redisUtil;
    }

}
