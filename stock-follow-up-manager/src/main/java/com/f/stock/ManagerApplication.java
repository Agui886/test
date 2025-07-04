package com.f.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAsync
@EnableSwagger2
@ComponentScan(basePackages={"service","entity","com.f.stock.*","utils","config"})
@MapperScan("mapper")
public class ManagerApplication{
    public static void main(String[] args) {
        SpringApplication.run(ManagerApplication.class, args);
    }


}
