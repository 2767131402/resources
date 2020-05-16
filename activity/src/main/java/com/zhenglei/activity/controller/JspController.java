package com.zhenglei.activity.controller;

import com.zhenglei.activity.entity.Activiti;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JspController {
    @RequestMapping(value = "/yuangong")
    public String createActiviti(Activiti vac) {
        return "yuangong";
    }
}
