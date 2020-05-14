package com.zhenglei.annotation.interceptor;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhenglei.annotation.EnumMessageType;
import com.zhenglei.annotation.annotation.NotNull;
import com.zhenglei.annotation.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 自定义拦截器
 * 拦截IP
 */
@Component
public class ParameterInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ParameterInterceptor.class);

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        
        try {
			String messgae = "";
			Field[] fs = User.class.getDeclaredFields();
			for (Field f : fs) {
				NotNull notNull = f.getAnnotation(NotNull.class);
				if(!StringUtils.isEmpty(notNull)) {
					if(EnumMessageType.MESSAGE.equals(notNull.type())) {
						System.err.println("短信");
					}
					if(EnumMessageType.APP.equals(notNull.type())) {
						System.err.println("APP");
					}
					
					String name = f.getName();
					String parameter = request.getParameter(name);
					if(parameter==null || parameter=="") {
						messgae+=notNull.message();
					}
				}
			}
			if(messgae==null || messgae=="") {
				return true;
			}else {
				response.getWriter().println(messgae);
				return false;
			}
		} catch (Exception e) {
			log.error("异常。。。");
			return false;
		}
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    	
    }

    /**
     * //在整个请求结束之后被调用
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}

