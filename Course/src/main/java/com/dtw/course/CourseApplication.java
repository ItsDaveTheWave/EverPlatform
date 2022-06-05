package com.dtw.course;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.course.config.FeignDecoder;
import com.dtw.course.util.CourseToDtoConverter;
import com.dtw.course.util.DtoToCourseConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.Decoder;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
@EnableFeignClients
public class CourseApplication implements WebMvcConfigurer {

	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	
	public static void main(String[] args) {
		SpringApplication.run(CourseApplication.class, args);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new CourseToDtoConverter());
		registry.addConverter(new DtoToCourseConverter());
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean
	public Decoder decoder() {
		return new FeignDecoder();
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	   return builder
			   .basicAuthentication(clientId, clientSecret)
			   .build();
	}
}