package com.jdbc.dynamicdatasource.aop;

import com.jdbc.dynamicdatasource.annotation.DataSource;
import com.jdbc.dynamicdatasource.config.DynamicDataSourceContextHolder;
import com.jdbc.dynamicdatasource.config.LoadStrategy;
import com.jdbc.dynamicdatasource.constant.DataSourceType;
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
 * @description 数据源切换(切入点和切面)
 */
@Aspect
@Component
@Order(0)
public class DynamicDataSourceAopAspect {
    static Logger logger = LoggerFactory.getLogger(DynamicDataSourceAopAspect.class);

    @Autowired
    private LoadStrategy loadStrategy;

    @Before("@annotation(ds), execution(* com.jdbc.dynamicdatasource.*.insert*(..)) || execution(* com.jdbc.dynamicdatasource.*.update*(..)) || execution(* com.jdbc.dynamicdatasource.*.delete*(..))")
    public void setWriteDataSourceType(JoinPoint point, DataSource ds) {
        if(StringUtils.isBlank(ds.value())){
            logger.debug("Use DataSource :{} >", DataSourceType.MASTER);
            DynamicDataSourceContextHolder.setDataSourceRouterKey(DataSourceType.MASTER);
        }
    }

    @Before("@annotation(ds), execution(* com.jdbc.dynamicdatasource.biz.user.mapper.*.select*(..)) || execution(* com.jdbc.dynamicdatasource.*.find*(..))")
    public void setReadDataSourceType(JoinPoint point, DataSource ds) {
        String datasource = loadStrategy.choiceDataSource(DataSourceType.SLAVE);

        if(StringUtils.isBlank(datasource)) {
            if (!DynamicDataSourceContextHolder.containsDataSource(datasource)) {
                throw new RuntimeException("数据源[" + datasource + "]不存在");
            }
            logger.debug("Use DataSource :{} >", datasource);
            DynamicDataSourceContextHolder.setDataSourceRouterKey(datasource);
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DataSource ds) {
        if(StringUtils.isBlank(ds.value())) {
            logger.debug("Revert DataSource : " + ds.value() + " > " + point.getSignature());
            DynamicDataSourceContextHolder.removeDataSourceRouterKey();
        }
    }

}