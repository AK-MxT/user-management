package com.user.mng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// AUTHORIZE
				.authorizeRequests()
				.mvcMatchers("/user/test").permitAll()
				.anyRequest()
				.authenticated()
				.and()
				// LOGIN
				.formLogin()
				/* */.defaultSuccessUrl("/user/test")
		// end
		;
	}
}