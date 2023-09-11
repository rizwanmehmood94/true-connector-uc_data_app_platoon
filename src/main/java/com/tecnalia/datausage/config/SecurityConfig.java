package com.tecnalia.datausage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            //HTTP Basic authentication
        .httpBasic()
        .and()
        .sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
        .authorizeRequests()
        .antMatchers("/proxy").hasRole("PROXY")
        .antMatchers("/about/**").permitAll()
		.antMatchers("/error").permitAll()
		.antMatchers("/data").permitAll()
		.antMatchers("/incoming-data-app/routerBodyBinary").permitAll()
        .and()
        .csrf().disable()
        .formLogin().disable();
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}