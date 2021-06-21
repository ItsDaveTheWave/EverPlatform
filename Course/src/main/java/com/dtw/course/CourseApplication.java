package com.dtw.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dtw.course.util.CourseToDtoConverter;
import com.dtw.course.util.DtoToCourseConverter;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableResourceServer
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
    public ResourceServerTokenServices tokenService() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setClientId("web");
        tokenServices.setClientSecret("pass");
        tokenServices.setCheckTokenEndpointUrl("http://localhost:8000/oauth/check_token");
        return tokenServices;
    }
}