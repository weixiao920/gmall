package com.wang.gmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.HttpclientUtil;
import com.wang.gmall.annotations.LoginRequired;
import com.wang.gmall.util.CookieUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义拦截器
 *
 * @author 微笑
 * @date 2019/12/1 20:49
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 判断如果不是请求control方法直接返回true
         */
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        /**
         * 获取被拦截的访问方法的注解
         */
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        /**
         * 判断是否有该注解
         * 无则放行，说明不用拦截
         */
        if (methodAnnotation == null) {
            return true;
        }
        /**
         * 获取LoginRequired注解的属性值
         */
        boolean loginSuccess = methodAnnotation.loginSuccess();
        /**
         * 登陆成功的验证码
         */
        String token = "";
        /**
         * 从cookie获取token
         */
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        /**
         * 从url地址获取token
         */
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }
        /**
         * 调用认证中心进行认证
         */
        String success = "fail";
        Map<String,String> successMap = new HashMap<>();
        if(StringUtils.isNotBlank(token)){
            String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();// 从request中获取ip
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }
            String successJson  = HttpclientUtil.doGet("http://passport.gmall.com:8086/verify?token=" + token+"&currentIp="+ip);

            successMap = JSON.parseObject(successJson,Map.class);

            success = successMap.get("status");

        }

        if (loginSuccess) {
            /**
             *  loginSuccess==true,用户必须登录成功才可以继续访问
             *  认证失败:重定向登录
             *  认证成功:将token携带的用户信息写入,覆盖cookie
             */
            if(!success.equals("success")){
                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://passport.gmall.com:8086/index?originUrl="+requestURL);
                return false;
            }
            request.setAttribute("memberId","1");
            request.setAttribute("username","wang");
            if(StringUtils.isNotBlank(token)){
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
            }

        } else {
            /**
             *  loginSuccess==false,用户不用登陆也可以访问
             *  也需要认证
             */
            if(success.equals("success")){
                /**
                 * 验证通过
                 * 将token携带的用户信息写入
                 * 并覆盖cookie
                 */
                request.setAttribute("memberId","1");
                request.setAttribute("username","wang");
                if(StringUtils.isNotBlank(token)){
                    CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                }
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
