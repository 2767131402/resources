package com.jdbc.dynamicdatasource.aop;

import com.jdbc.dynamicdatasource.annotation.DataSource;
import com.jdbc.dynamicdatasource.config.DynamicDataSourceContextHolder;
import com.jdbc.dynamicdatasource.config.LoadStrategy;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 方法上有@DataSource注解时优先使用 注解中的数据源
 */
@Aspect
@Component
@Order(1)
public class DynamicDataSourceAnnotationAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAnnotationAspect.class);

    @Autowired
    private LoadStrategy loadStrategy;

    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, DataSource ds) throws Throwable {
        String datasource = loadStrategy.choiceDataSource(ds.value());
        //判断是否有注解
        if(StringUtils.isNotBlank(datasource)){
            if (!DynamicDataSourceContextHolder.containsDataSource(datasource)) {
                throw new RuntimeException("数据源[" + datasource + "]不存在");
            }
            logger.debug("Use DataSource:{} >", datasource);
            DynamicDataSourceContextHolder.setDataSourceRouterKey(datasource);
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DataSource ds) {
        if(StringUtils.isNotBlank(ds.value())) {
            logger.debug("Revert DataSource : " + ds.value() + " > " + point.getSignature());
            DynamicDataSourceContextHolder.removeDataSourceRouterKey();
        }
    }
}