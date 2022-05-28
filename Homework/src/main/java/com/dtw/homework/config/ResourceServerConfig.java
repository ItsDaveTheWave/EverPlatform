package com.dtw.homework.config;

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
			.antMatchers(HttpMethod.GET, "/api/homework").hasAnyRole("admin")
			.antMatchers(HttpMethod.GET, "/api/homework/{id}").hasAnyRole("admin", "student", "teacher")
			.antMatchers(HttpMethod.GET, "/api/homework/{id}/download").hasAnyRole("admin", "student", "teacher")
			.antMatchers(HttpMethod.POST, "/api/homework").hasAnyRole("admin", "student")
			.antMatchers(HttpMethod.DELETE, "/api/homework/{id}").hasAnyRole("admin", "student")
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