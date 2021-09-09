package com.user.mng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

	/**
	 * リダイレクト処理
	 * ルートにアクセスがあった場合は一覧画面へリダイレクトする
	 * 未ログイン状態であればログイン画面へさらにリダイレクトされる
	 *
	 * @return ユーザ一覧画面
	 */
	@GetMapping(value = "/")
	public String redirect() {
		return "redirect:/user/list/1";
	}
}
