package com.zhenglei.annotation.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器，拦截请求
 *
 * @Description:
 * @Date: 2018/9/11
 * @Auther: zhenglei
 */
@SuppressWarnings("deprecation")
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ParameterInterceptor parameterInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //参数拦截器
    	//拦截所有请求
        registry.addInterceptor(parameterInterceptor).addPathPatterns("/**");
//                .excludePathPatterns("/tel/*").excludePathPatterns("/error/*");
        super.addInterceptors(registry);
    }
}

