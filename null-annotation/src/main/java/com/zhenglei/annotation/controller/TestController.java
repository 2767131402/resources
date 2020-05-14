package com.zhenglei.annotation.controller;

import com.zhenglei.annotation.domain.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/test")
	public User test(User user) {
		return user;
	}
}
