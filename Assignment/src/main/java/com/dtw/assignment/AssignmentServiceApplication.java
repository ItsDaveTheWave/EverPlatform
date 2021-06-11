package com.dtw.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AssignmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentServiceApplication.class, args);
	}
}