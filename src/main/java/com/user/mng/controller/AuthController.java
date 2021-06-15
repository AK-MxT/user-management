package com.user.mng.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.mng.domain.model.request.AccountRegisterRequestEntity;
import com.user.mng.domain.service.LoginServiceImpl;

@Controller
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
	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}

	/**
	 * アカウント登録画面
	 *
	 * @param model
	 * @return アカウント登録画面
	 */
	@RequestMapping(value = "/signup")
	public String signup() {
		return "signup";
	}

	/**
	 * アカウント登録
	 *
	 * @param AccountRegisterRequestEntity アカウント登録用のリクエストエンティティ
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/register")
	public String register(@Validated @ModelAttribute AccountRegisterRequestEntity accountRegisterRequestEntity,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", accountRegisterRequestEntity);

			return "signup";
		}

		// TODO ↑リクエストエンティティ作る・バリデーションも作る
		loginServiceImpl.registerAccount(accountRegisterRequestEntity.getUserName(),
				passwordEncorder.encode(accountRegisterRequestEntity.getPassword()));

		return "login";
	}
}
