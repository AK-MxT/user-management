package com.user.mng.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.request.UserConfirmRequestEntity;
import com.user.mng.domain.model.request.UserEditRequestEntity;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
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
	@RequestMapping(value = "/list/{page}")
	public String list(@PathVariable @Validated Integer page, UserListRequestEntity userListRequestEntity, Model model, BindingResult result) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("validationError", errorList);
			model.addAttribute("searchItems", userListRequestEntity);

			return "list";
		}

		UserListResponseEntity list = userService.getUserList(userListRequestEntity);

		model.addAttribute("searchItems", userListRequestEntity);
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

		UserEditResponseEntity user = userService.getUserForEdit(id);

		model.addAttribute("title", UserConstant.TITLE_UPDATE);
		model.addAttribute("userForEdit", user);

		// src/main/resources/templates/edit.html を呼び出す
		return "edit";
	}

	/**
	 * ユーザ登録画面
	 * ユーザ登録のためのエンティティセット処理（登録メソッドではない）
	 *
	 * @param model
	 *
	 * @return ユーザ登録画面
	 */
	@RequestMapping(value = "/new")
	public String register(Model model) {

		model.addAttribute("title", UserConstant.TITLE_REGISTER);
		model.addAttribute("userForEdit", new UserEditRequestEntity());

		// src/main/resources/templates/edit.html を呼び出す
		return "edit";
	}

	/**
	 * ユーザ確認画面
	 * ユーザ登録更新時のチェック処理
	 *
	 * @param userConfirmRequestEntity
	 * @param model
	 *
	 * @return ユーザ確認画面
	 */
	@RequestMapping(value = "/confirm")
	public String confirm(@Validated @ModelAttribute UserConfirmRequestEntity userConfirmRequestEntity,
			BindingResult result, Model model) {

		if (Objects.isNull(userConfirmRequestEntity.getId())) {
			model.addAttribute("title", UserConstant.TITLE_REGISTER);
		} else {
			model.addAttribute("title", UserConstant.TITLE_UPDATE);
		}

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", userConfirmRequestEntity);

			return "edit";
		}

		model.addAttribute("userForEdit", userConfirmRequestEntity);

		// src/main/resources/templates/confirm.html を呼び出す
		return "confirm";
	}

	/**
	 * ユーザ更新
	 *
	 * @param userUpdateRequestEntity
	 * @param model
	 *
	 * @return ユーザ一覧画面
	 */
	@RequestMapping(value = "/update")
	public String update(@Validated @ModelAttribute UserEditRequestEntity userEditRequestEntity,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("title", UserConstant.TITLE_UPDATE);
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", userEditRequestEntity);

			return "edit";
		}

		// ユーザ更新処理
		userService.updateUser(userEditRequestEntity);

		redirectAttributes.addFlashAttribute("information", UserConstant.UPDATE_SUCCESS);

		// 更新後は一覧画面へリダイレクト
		return "redirect:/user/list";
	}

	/**
	 * ユーザ登録
	 *
	 * @param userUpdateRequestEntity
	 * @param model
	 *
	 * @return ユーザ一覧画面
	 */
	@RequestMapping(value = "/insert")
	public String insert(@Validated @ModelAttribute UserEditRequestEntity userEditRequestEntity,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("title", UserConstant.TITLE_REGISTER);
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", userEditRequestEntity);

			return "edit";
		}

		// ユーザ登録処理
		userService.insertUser(userEditRequestEntity);

		redirectAttributes.addFlashAttribute("information", UserConstant.REGISTER_SUCCESS);

		// 登録後後は一覧画面へリダイレクト
		return "redirect:/user/list";
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
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		userService.deleteUser(id);

		redirectAttributes.addFlashAttribute("information", UserConstant.DELETE_SUCCESS);

		// 削除後は一覧画面へリダイレクト
		return "redirect:/user/list";
	}
}
