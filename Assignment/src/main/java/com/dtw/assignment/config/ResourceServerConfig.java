package com.dtw.assignment.config;

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
			.antMatchers(HttpMethod.GET, "/api/assignment").hasAnyRole("admin")
			.antMatchers(HttpMethod.GET, "/api/assignment/{id}").hasAnyRole("admin", "student", "teacher")
			.antMatchers(HttpMethod.POST, "/api/assignment").hasAnyRole("admin", "teacher")
			.antMatchers(HttpMethod.DELETE, "/api/assignment/{id}").hasAnyRole("admin", "teacher")
			.and()
			.formLogin().disable()
			.logout().disable()
			.httpBasic().disable()
			.csrf().disable()
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