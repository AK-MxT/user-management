package com.user.mng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.mng.domain.service.LoginServiceImpl;

@Controller
@RequestMapping(value = "/login")
public class AuthController {

	@Autowired
	private LoginServiceImpl loginServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncorder;

	/**
	 * ログイン画面
	 *
	 * @param model
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/")
	public String login() {
		return "login";
	}

	/**
	 * アカウント登録
	 *
	 * @param AccountRegisterRequestEntity アカウント登録用のリクエストエンティティ
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/register")
	public String register(String userName, String password) {

		// TODO ↑リクエストエンティティ作る・バリデーションも作る
		loginServiceImpl.registerAccount(userName, passwordEncorder.encode(password));

		return "login";
	}
}
