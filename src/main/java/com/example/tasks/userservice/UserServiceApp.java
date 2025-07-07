package com.example.tasks.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class UserServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApp.class, args);
	}
}
