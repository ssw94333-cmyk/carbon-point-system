package com.carbon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.carbon.mapper")
public class CarbonPointSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarbonPointSystemApplication.class, args);
    }
}
