package com.user.mng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

	/**
	 * ログイン画面
	 *
	 * @param model
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}

}
