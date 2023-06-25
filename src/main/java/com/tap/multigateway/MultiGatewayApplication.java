package com.tap.multigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.tap.*"})
@EnableFeignClients
public class MultiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiGatewayApplication.class, args);
    }

}
