package com.wang.gmall.config;

import com.wang.gmall.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author 微笑
 * @date 2019/12/1 20:43
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    AuthInterceptor authInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
    }




}
