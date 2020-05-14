package com.jdbc.dynamicdatasource.config;

import com.jdbc.dynamicdatasource.constant.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 自定义从库选择策略
 */
public class LoadStrategy {
    private static Logger logger = LoggerFactory.getLogger(LoadStrategy.class);

    public String choiceDataSource(String dataSource){
        //判断是否是从数据库
        if (DataSourceType.SLAVE.equalsIgnoreCase(dataSource)) {
            //当前时间和从节点个数取余数+1
            int index = ((int) System.currentTimeMillis()) % (DynamicDataSourceContextHolder.slaveDataSourceIds.size())+1;
            if (index > DynamicDataSourceContextHolder.slaveDataSourceIds.size()) {
                logger.error("计算的从数据库下标({})大于从数据总数({})",index,DynamicDataSourceContextHolder.slaveDataSourceIds.size());
                index = 1;
            }
            dataSource = dataSource + index;
        }
        return dataSource;
    }

}
