package com.zhenglei.ratelimiter.interceptor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RateLimiterInterceptor extends HandlerInterceptorAdapter {
    private static LoadingCache<String,RateLimiter> accessLimitCache= CacheBuilder.
            newBuilder().expireAfterWrite(1, TimeUnit.DAYS)//每天清空
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String s) throws Exception {
                    //新的用户，每秒只能发出N个令牌
                    return RateLimiter.create(1);
                }
            });

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json; charset=UTF-8");
        ServletOutputStream output = response.getOutputStream();

        String token = request.getParameter("token");
        if(StringUtils.isEmpty(token)){
            output.write("token不存在".getBytes());
            output.flush();
        }

        if(handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod)handler;
            token = token+"_"+h.getMethod().getName();
        }

        try {
            RateLimiter rateLimiter = accessLimitCache.get(token);
            if(!rateLimiter.tryAcquire()){
                output.println(token+" 你访问的频率太快了");
                output.flush();
            }
        } catch (ExecutionException e) {
//            throw new Exception("Access limit error:"+e.getMessage());
        }
        return true;
    }
}
