package com.hirisun.cloud.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.hirisun.cloud"})
@MapperScan({"com.hirisun.cloud.platform.information.mapper","com.hirisun.cloud.platform.document.mapper"})
@EnableDiscoveryClient
@EnableFeignClients("com.hirisun.cloud.api.*")
public class PlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
    }

}
