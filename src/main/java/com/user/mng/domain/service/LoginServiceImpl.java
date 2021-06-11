package com.user.mng.domain.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.user.mng.constant.AuthConstant;
import com.user.mng.domain.repository.TrnUserMapper;

@Service
public class LoginServiceImpl implements UserDetailsService {

	private final TrnUserMapper trnUserMapper;

	public LoginServiceImpl(TrnUserMapper trnUserMapper) {
		this.trnUserMapper = trnUserMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (StringUtils.isEmpty(username)) {
			throw new UsernameNotFoundException("username is empty.");
		}

		// パスワードの設定
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String password = encoder.encode("adminpassword");

		// 権限の設定
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(AuthConstant.ROLE_ADMIN));

		// ユーザー情報を作成
		User user = new User(username, password, authorities);
		return user;
	}

}
