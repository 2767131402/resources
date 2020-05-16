package com.bbd.elasticsearch.controler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    @RequestMapping("/hello")
    public String test(){
        return "hello";
    }

    @RequestMapping("/test")
    public String test1(HttpServletRequest request){
        System.out.println(request.getRequestURL());
        System.out.println(request.getRequestURI());
        String requestURL = request.getRequestURL().toString();
        System.out.println(requestURL.replace(request.getRequestURI(),""));
        System.out.println(request.getContextPath());
        System.out.println(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort());
        System.out.println(request.getRemoteHost());
        return "hello";
    }
}
