package com.kuangyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.viewshine.mall")
@SpringBootApplication
public class MybatisPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPluginApplication.class);
    }
}
