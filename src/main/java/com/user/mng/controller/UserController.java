package com.user.mng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/test") // URLは、http://localhost:8080/test
	public String test(Model model) {

		System.out.println(model);

		// viewに表示する res を設定する
		model.addAttribute("res", "てすと");

		// src/main/resources/templates/test.html を呼び出す
		return "test";
	}

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

		// src/main/resources/templates/list.html を呼び出す
		return "detail";
	}
}
