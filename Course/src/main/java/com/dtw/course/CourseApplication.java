package com.dtw.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.course.util.CourseToDtoConverter;
import com.dtw.course.util.DtoToCourseConverter;

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
}