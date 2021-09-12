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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.user.mng.constant.AuthConstant;
import com.user.mng.domain.model.request.AccountRegisterRequestEntity;
import com.user.mng.domain.service.LoginServiceImpl;
import com.user.mng.exceptions.ServiceLogicException;

@Controller
public class AuthController {

	@Autowired
	private LoginServiceImpl loginServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncorder;

	/**
	 * ログイン画面
	 *
	 * @param error ログインエラー時は「?error」のパラメータが付いてくる
	 * @param model Modelインスタンス
	 *
	 * @return ログイン画面
	 */
	@GetMapping(value = "/login")
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
	 * @param model Modelインスタンス
	 *
	 * @return アカウント登録画面
	 */
	@GetMapping(value = "/signup")
	public String signup(Model model) {
		model.addAttribute("userForEdit", new AccountRegisterRequestEntity());
		return "signup";
	}

	/**
	 * アカウント登録
	 *
	 * @param accountRegisterRequestEntity アカウント登録用のリクエストエンティティ
	 * @param result バリデーション結果格納用クラス
	 * @param model Modelインスタンス
	 * @param redirectAttributes リダイレクト先へ値を渡すためのクラス
	 *
	 * @return ログイン画面
	 */
	@PostMapping(value = "/register")
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

		try {
			// アカウント登録処理
			loginServiceImpl.registerAccount(accountRegisterRequestEntity.getUserName(),
					passwordEncorder.encode(accountRegisterRequestEntity.getPassword()));
		} catch (ServiceLogicException e) {
			model.addAttribute("validationError", e.getMessage());
			model.addAttribute("userForEdit", accountRegisterRequestEntity);

			return "signup";
		}

		redirectAttributes.addFlashAttribute("information", AuthConstant.REGISTER_SUCCESS);

		return "redirect:/login";
	}
}
