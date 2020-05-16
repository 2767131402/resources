package com.zhenglei.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class StreamUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamUploadApplication.class, args);
    }

}
