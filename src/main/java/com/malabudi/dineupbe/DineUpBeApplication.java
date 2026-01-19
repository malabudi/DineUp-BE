package com.malabudi.dineupbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/")
public class DineUpBeApplication {

    public static void main(String[] args) {
        System.out.println("JWT_SECRET from env: " + System.getenv("JWT_SECRET"));
        SpringApplication.run(DineUpBeApplication.class, args);
    }

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }
}
