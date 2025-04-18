package com.yuhao.yupicturebackend.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;
    private String port;
    private String password;
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        String redisAddress =String.format("redis://%s:%s",host,port) ;
        config.useSingleServer().setAddress(redisAddress).setDatabase(2).setPassword(password);

        RedissonClient redisson = Redisson.create(config);
        return  redisson;
    }
}
