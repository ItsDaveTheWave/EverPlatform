package com.dtw.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.homework.util.DtoToHomeworkConverter;
import com.dtw.homework.util.HomeworkToDtoConverter;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
public class HomeworkApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(HomeworkApplication.class, args);
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new HomeworkToDtoConverter());
		registry.addConverter(new DtoToHomeworkConverter());
	}
}