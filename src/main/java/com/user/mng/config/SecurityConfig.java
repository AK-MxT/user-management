package com.user.mng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// パスワードのアルゴリズム設定
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		auth
			.inMemoryAuthentication()
			.withUser("user")
			.password(encoder.encode("password"))
			.roles("USER")
			.and()
			.withUser("admin")
			.password(encoder.encode("password"))
			.roles("USER", "ADMIN");
	}

	/**
	 * 静的リソースを認可処理の対象外とする
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(
				"/images/**",
				"/css/**",
				"/javascript/**",
				"/webjars/**");
	}

	/**
	 * 認証・認可設定
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				//「login.html」はログイン不要でアクセス可能に設定
				.antMatchers("/login").permitAll()
				//上記以外は直リンク禁止
				.anyRequest().authenticated()
			.and()
			.formLogin()
				//ログイン処理のパス
				.loginProcessingUrl("/login")
				//ログインページ
				.loginPage("/login")
				//ログインエラー時の遷移先 ※パラメーターに「error」を付与
				.failureUrl("/login?error")
				.defaultSuccessUrl("/user/list")
				//ログイン時のキー：名前
				.usernameParameter("name")
				//ログイン時のパスワード
				.passwordParameter("password")
			.and()
			.logout()
				//ログアウト時の遷移先 POSTでアクセス
				.logoutSuccessUrl("/login");
		;
	}
}