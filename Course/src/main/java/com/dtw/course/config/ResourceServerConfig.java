package com.dtw.course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${security.oauth2.resource.id}")
	private String resourceId;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/api/course").hasAnyRole("admin")
			.antMatchers(HttpMethod.GET, "/api/course/{id}").hasAnyRole("admin", "student", "teacher")
			.antMatchers(HttpMethod.POST, "/api/course").hasAnyRole("teacher")
			.antMatchers(HttpMethod.DELETE, "/api/course/{id}").hasAnyRole("teacher")
			.antMatchers(HttpMethod.GET, "/api/course/{id}/assignment").hasAnyRole("admin", "teacher", "student")
			.antMatchers(HttpMethod.GET, "/api/course/{id}/assignment/{assignmentId}").hasAnyRole("admin", "teacher", "student")
			.antMatchers(HttpMethod.POST, "/api/course/{id}/assignment").hasAnyRole("teacher")
			.antMatchers(HttpMethod.DELETE, "/api/course/{id}/assignment/{assignmentId}").hasAnyRole("teacher")
			.antMatchers(HttpMethod.GET, "/api/course/{id}/assignment/{assignmentId}/homework").hasAnyRole("admin", "teacher")
			.antMatchers(HttpMethod.GET, "/api/course/{id}/assignment/{assignmentId}/homework/{homeworkId}").hasAnyRole("admin", "teacher", "student")
			.antMatchers(HttpMethod.GET, "/api/course/{id}/assignment/{assignmentId}/homework/{homeworkId}/download").hasAnyRole("admin", "teacher", "student")
			.antMatchers(HttpMethod.POST, "/api/course/{id}/assignment/{assignmentId}/homework").hasAnyRole("student")
			.antMatchers(HttpMethod.DELETE, "/api/course/{id}/assignment/{assignmentId}/homework/{homeworkId}").hasAnyRole("student")
			.antMatchers(HttpMethod.GET, "/api/course/student/{username}").hasAnyRole("admin", "student")
			.antMatchers(HttpMethod.GET, "/api/course/teacher/{username}").hasAnyRole("admin", "teacher")
			.antMatchers("/h2-console").permitAll()
			.and()
			.formLogin().disable()
			.logout().disable()
			.httpBasic().disable()
			.csrf().disable()
			.headers().frameOptions().disable()
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint())
			.accessDeniedHandler(accessDeniedHandler());
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources
			.resourceId(resourceId);
	}
	
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new AccessDeniedHandlerImp();
	}
	
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new AuthenticationEntryPointImp();
	}
}