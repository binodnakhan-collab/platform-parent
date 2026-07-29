package com.platform.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.platform"})
public class IdentityAccessManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityAccessManagementApplication.class, args);
    }
}
