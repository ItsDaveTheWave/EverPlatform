package com.dtw.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class OAuthService8Application extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(OAuthService8Application.class, args);
	}
}