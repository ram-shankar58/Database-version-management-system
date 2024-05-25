package com.example.database_version_management_system;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VersionManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VersionManagementSystemApplication.class, args);
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
