package com.icestudyroom_email.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EmailSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmailSystemApplication.class, args);
	}
}
