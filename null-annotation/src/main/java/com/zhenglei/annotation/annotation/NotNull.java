package com.zhenglei.annotation.annotation;

import com.zhenglei.annotation.EnumMessageType;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义注解
 * 非空注解
 * @Description:
 * @Date: 2019年4月8日
 * @auther: zhenglei
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface NotNull {
	/**
	 * 为空时返回的消息
	 */
	String message();
	
	/**
	 * 消息类型
	 */
	EnumMessageType type();

}