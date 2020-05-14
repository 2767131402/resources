package com.jdbc.dynamicdatasource;

import com.jdbc.dynamicdatasource.config.LoadStrategy;
import com.jdbc.dynamicdatasource.register.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import(DynamicDataSourceRegister.class)
@MapperScan("com.jdbc.dynamicdatasource")
@SpringBootApplication
@EnableTransactionManagement
public class DynamicdatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicdatasourceApplication.class, args);
    }

    /**
     * 从库选择策略
     * @return
     */
    @Bean
    public LoadStrategy loadStrategy(){
        return new LoadStrategy();
    }

}
