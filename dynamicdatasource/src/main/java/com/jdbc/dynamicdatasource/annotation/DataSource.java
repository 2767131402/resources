package com.jdbc.dynamicdatasource.annotation;

import com.jdbc.dynamicdatasource.constant.DataSourceType;

import java.lang.annotation.*;

/**
 * 切换数据注解 可以用于类或者方法级别 方法级别优先级 > 类级别
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    /**
     * value 选择主库还是从库
     * 参考：DataSourceType
     */
    String value() default DataSourceType.MASTER;
}