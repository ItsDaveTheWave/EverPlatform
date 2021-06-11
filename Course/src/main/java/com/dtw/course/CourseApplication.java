package com.dtw.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.course.util.CourseToDtoConverter;
import com.dtw.course.util.DtoToCourseConverter;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class CourseApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(CourseApplication.class, args);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new CourseToDtoConverter());
		registry.addConverter(new DtoToCourseConverter());
	}
	
	@Bean
	public RequestInterceptor requestTokenBearerInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				JwtAuthenticationToken jwt = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
				
				template.header("Authorization", "Bearer " + jwt.getToken().getTokenValue());
			}
		};
	}
}