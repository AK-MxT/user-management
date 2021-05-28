package com.user.mng.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.mng.domain.model.TrnUser;
import com.user.mng.domain.model.request.UserConfirmRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * ユーザ一覧画面
	 *
	 * @param model
	 * @return ユーザ一覧画面
	 */
	@RequestMapping(value = "/list")
	public String list(Model model) {

		UserListResponseEntity list = userService.getUserList();

		model.addAttribute("list", list.getUserList());

		// src/main/resources/templates/list.html を呼び出す
		return "list";
	}

	/**
	 * ユーザ詳細画面
	 *
	 * @param id
	 * @param model
	 *
	 * @return ユーザ詳細画面
	 */
	@RequestMapping(value = "/detail/{id}")
	public String detail(@PathVariable Long id, Model model) {

		UserDetailResponseEntity user = userService.getUser(id);

		model.addAttribute("userDetail", user.getUser());

		// src/main/resources/templates/detail.html を呼び出す
		return "detail";
	}

	/**
	 * ユーザ更新画面
	 * ユーザ更新のためのデータ取得処理（更新メソッドではない）
	 *
	 * @param id
	 * @param model
	 *
	 * @return ユーザ詳細画面
	 */
	@RequestMapping(value = "/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {

		TrnUser user = userService.getUserForEdit(id);

		model.addAttribute("userForEdit", user);

		// src/main/resources/templates/update.html を呼び出す
		return "update";
	}

	/**
	 * ユーザ確認画面
	 * ユーザ登録更新時のチェック処理
	 *
	 * @param userConfirmRequest
	 * @param model
	 *
	 * @return ユーザ確認画面
	 */
	@RequestMapping(value = "/confirm")
	public String confirm(@Validated @ModelAttribute UserConfirmRequestEntity userConfirmRequestEntity,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", userConfirmRequestEntity);

			return "update";
		}

		// model.addAttribute("userForEdit", user);

		// src/main/resources/templates/update.html を呼び出す
		return "confirm";
	}

	/**
	 * ユーザ削除処理
	 *
	 * @param id
	 * @param model
	 *
	 * @return ユーザ詳細画面
	 */
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id, Model model) {

		userService.deleteUser(id);

		// 削除後は一覧画面へリダイレクト
		return "redirect:/user/list";
	}
}
