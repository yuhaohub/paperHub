package com.yuhao.yupicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})

@MapperScan("com.yuhao.yupicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableScheduling  // 启用 Spring 定时任务
public class YuPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuPictureBackendApplication.class, args);
    }

}
