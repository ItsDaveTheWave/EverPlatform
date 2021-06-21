package com.dtw.oauth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(1)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .authorizeRequests()
          .antMatchers("/oauth/user/**")
          .permitAll()
          .antMatchers("/oauth/admin/**")
          .permitAll()
          .antMatchers("/oauth/student/**")
          .permitAll()
          .antMatchers("/oauth/teacher/**")
          .permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
        	.jdbcAuthentication()
        	.dataSource(dataSource)
        	.usersByUsernameQuery("select username, password, enabled from user where username=?")
        	.authoritiesByUsernameQuery("select user.username, role.name from user join user_role on user.id = user_role.user_id join role on user_role.role_id = role.id where username = ?")
        	.passwordEncoder(passwordEncoder());
    }
     
    @Bean
    public PasswordEncoder passwordEncoder(){ 
        return new BCryptPasswordEncoder(); 
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}