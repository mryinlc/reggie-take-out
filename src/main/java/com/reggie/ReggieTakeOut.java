package com.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@MapperScan("com.reggie.mapper")
@EnableTransactionManagement
@EnableCaching
public class ReggieTakeOut {
    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOut.class, args);
    }
}
