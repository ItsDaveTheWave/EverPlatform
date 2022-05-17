package com.dtw.assignment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.assignment.util.AssignmentToDtoConverter;
import com.dtw.assignment.util.DtoToAssignmentConverter;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
public class AssignmentServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentServiceApplication.class, args);
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new AssignmentToDtoConverter());
		registry.addConverter(new DtoToAssignmentConverter());
	}
}