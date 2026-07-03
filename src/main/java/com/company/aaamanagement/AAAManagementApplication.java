package com.company.aaamanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AAAManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(AAAManagementApplication.class, args);
    }
}
