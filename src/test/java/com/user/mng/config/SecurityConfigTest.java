package com.user.mng.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.user.mng.constant.AuthConstant;

@Configuration
public class SecurityConfigTest {

	@Bean
	public UserDetailsService userDetailsService() {
		//		return new UserDetailsService() {
		//			@Override
		//			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//				return User
		//						.withUsername(username)
		//						.password("password")
		//						.authorities(new SimpleGrantedAuthority("ROLE_USER"))
		//						.build();
		//			}
		//		};
		GrantedAuthority authority = new SimpleGrantedAuthority(AuthConstant.ROLE_USER);
		UserDetails userDetails = (UserDetails) new User("user001", "test", Arrays.asList(authority));
		return new InMemoryUserDetailsManager(Arrays.asList(userDetails));
	}
}
