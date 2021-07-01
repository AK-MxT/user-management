package com.user.mng.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.user.mng.constant.AuthConstant;
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
	 * @param error
	 * @param model
	 *
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/login")
	public String login(@RequestParam(value = "error", required = false) String error, Model model) {

		if (Objects.nonNull(error)) {
			List<String> errorList = new ArrayList<String>();
			errorList.add(AuthConstant.LOGIN_ERROR);
			model.addAttribute("validationError", errorList);
		}

		return "login";
	}

	/**
	 * アカウント登録画面
	 *
	 * @param model
	 * @return アカウント登録画面
	 */
	@RequestMapping(value = "/signup")
	public String signup(Model model) {
		model.addAttribute("userForEdit", new AccountRegisterRequestEntity());
		return "signup";
	}

	/**
	 * アカウント登録
	 *
	 * @param accountRegisterRequestEntity アカウント登録用のリクエストエンティティ
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/register")
	public String register(@Validated @ModelAttribute AccountRegisterRequestEntity accountRegisterRequestEntity,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", accountRegisterRequestEntity);

			return "signup";
		}

		loginServiceImpl.registerAccount(accountRegisterRequestEntity.getUserName(),
				passwordEncorder.encode(accountRegisterRequestEntity.getPassword()));

		redirectAttributes.addFlashAttribute("information", AuthConstant.REGISTER_SUCCESS);

		return "redirect:/login";
	}
}
