package com.dtw.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.user.util.DtoToUserConverter;
import com.dtw.user.util.UserToDtoConverter;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
public class UserServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new UserToDtoConverter());
		registry.addConverter(new DtoToUserConverter());
	}
}