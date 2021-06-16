package com.user.mng.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.mng.domain.service.LoginServiceImpl;

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	LoginServiceImpl service;

    /**
     * DBでユーザ認証を行うための処理
     *
     * @param auth 認証マネージャー生成ツール
     * @throws Exception
     */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// DB認証（パスワードはBCryptで暗号化）
		auth.userDetailsService(service).passwordEncoder(passwordEncoder());
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
				//「login.html / signup.html」はログイン不要でアクセス可能に設定
				// 「/register」はアカウント登録用のAPIのURIなので許可する
				.antMatchers("/login", "/signup", "/register").permitAll()
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
				//ログイン時のキー：ユーザ名
				.usernameParameter("userName")
				//ログイン時のパスワード
				.passwordParameter("password")
			.and()
			.logout()
				//ログアウト時の遷移先 POSTでアクセス
				.logoutSuccessUrl("/login");
	}

	/**
	 * パスワードをBCryptで暗号化するクラス
	 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}