package com.example.SoftbinatorProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SoftbinatorProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftbinatorProjectApplication.class, args);
    }

}
