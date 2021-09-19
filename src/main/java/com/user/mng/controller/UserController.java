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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.request.UserConfirmRequestEntity;
import com.user.mng.domain.model.request.UserEditRequestEntity;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.service.UserService;
import com.user.mng.exceptions.DataNotFoundException;
import com.user.mng.exceptions.ServiceLogicException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * ユーザ一覧画面
	 *
	 * @param page ページ番号
	 * @param userListRequestEntity 検索条件
	 * @param result バリデーション結果格納用クラス
	 * @param model Modelインスタンス
	 *
	 * @return ユーザ一覧画面
	 */
	@RequestMapping(value = "/list/{page}", method = { RequestMethod.GET, RequestMethod.POST })
	public String list(@PathVariable Integer page,
			@Validated @ModelAttribute UserListRequestEntity userListRequestEntity, BindingResult result, Model model) {

		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("validationError", errorList);
			model.addAttribute("searchItems", userListRequestEntity);
			model.addAttribute("list", new ArrayList<>());

			return "list";
		}

		UserListResponseEntity list = userService.getUserList(page, userListRequestEntity);

		model.addAttribute("page", page);
		model.addAttribute("searchItems", userListRequestEntity);
		model.addAttribute("list", list.getUserList());

		// src/main/resources/templates/list.html を呼び出す
		return "list";
	}

	/**
	 * ユーザ詳細画面
	 *
	 * @param id 取得対象ID
	 * @param model Modelインスタンス
	 * @param redirectAttributes リダイレクト先へ値を渡すためのクラス
	 *
	 * @return ユーザ詳細画面
	 */
	@GetMapping(value = "/detail/{id}")
	public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

		UserDetailResponseEntity user = new UserDetailResponseEntity();

		try {
			user = userService.getUser(id);
		} catch (DataNotFoundException e) {
			log.error(e.getClass().getSimpleName(), e);
			redirectAttributes.addFlashAttribute("exception", e.getMessage());

			// エラー時は一覧画面へ戻す
			return "redirect:/user/list/1";
		}

		model.addAttribute("userDetail", user.getUser());

		// src/main/resources/templates/detail.html を呼び出す
		return "detail";
	}

	/**
	 * ユーザ更新画面
	 * ユーザ更新のためのデータ取得処理（更新メソッドではない）
	 *
	 * @param id 更新対象ID
	 * @param model Modelインスタンス
	 * @param redirectAttributes リダイレクト先へ値を渡すためのクラス
	 *
	 * @return ユーザ更新画面
	 */
	@GetMapping(value = "/edit/{id}")
	public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

		UserEditResponseEntity user = new UserEditResponseEntity();

		try {
			user = userService.getUserForEdit(id);
		} catch (DataNotFoundException e) {
			log.error(e.getClass().getSimpleName(), e);
			redirectAttributes.addFlashAttribute("exception", e.getMessage());

			// エラー時は一覧画面へ戻す
			return "redirect:/user/list/1";
		}

		model.addAttribute("title", UserConstant.TITLE_UPDATE);
		model.addAttribute("userForEdit", user);

		// src/main/resources/templates/edit.html を呼び出す
		return "edit";
	}

	/**
	 * 確認画面から戻る
	 * 更新確認画面から戻る際の処理
	 *
	 * @param id 更新対象ID
	 * @param userConfirmRequestEntity 確認画面で保持している更新データを格納するエンティティ
	 * @param model Modelインスタンス
	 *
	 * @return ユーザ更新画面
	 */
	@PostMapping(value = "/edit/{id}")
	public String edit(@PathVariable Long id, UserConfirmRequestEntity userConfirmRequestEntity, Model model) {

		model.addAttribute("title", UserConstant.TITLE_UPDATE);
		model.addAttribute("userForEdit", userConfirmRequestEntity);

		// src/main/resources/templates/edit.html を呼び出す
		return "edit";
	}

	/**
	 * ユーザ登録画面
	 * ユーザ登録のためのエンティティセット処理（登録メソッドではない）
	 *
	 * @param model Modelインスタンス
	 *
	 * @return ユーザ登録画面
	 */
	@GetMapping(value = "/new")
	public String register(Model model) {

		model.addAttribute("title", UserConstant.TITLE_REGISTER);
		model.addAttribute("userForEdit", new UserEditRequestEntity());

		// src/main/resources/templates/edit.html を呼び出す
		return "edit";
	}

	/**
	 * 確認画面から戻る
	 * 登録確認画面から戻る際の処理
	 *
	 * @param userConfirmRequestEntity 確認画面で保持している登録データを格納するエンティティ
	 * @param model Modelインスタンス
	 *
	 * @return ユーザ登録画面
	 */
	@PostMapping(value = "/new")
	public String register(UserConfirmRequestEntity userConfirmRequestEntity, Model model) {

		model.addAttribute("title", UserConstant.TITLE_REGISTER);
		model.addAttribute("userForEdit", userConfirmRequestEntity);

		// src/main/resources/templates/edit.html を呼び出す
		return "edit";
	}

	/**
	 * ユーザ確認画面
	 * ユーザ登録更新時のチェック処理
	 *
	 * @param userConfirmRequestEntity 登録・更新データを格納するエンティティ
	 * @param result バリデーション結果格納用クラス
	 * @param model Modelインスタンス
	 *
	 * @return ユーザ確認画面
	 */
	@PostMapping(value = "/confirm")
	public String confirm(@Validated @ModelAttribute UserConfirmRequestEntity userConfirmRequestEntity,
			BindingResult result, Model model) {

		if (Objects.isNull(userConfirmRequestEntity.getId())) {
			model.addAttribute("title", UserConstant.TITLE_REGISTER);

			try {
				// DBへの登録可能件数制限に達していないかチェック
				userService.checkUserCount();
			} catch (ServiceLogicException e) {
				log.error(e.getClass().getSimpleName(), e);
				model.addAttribute("validationError", e.getMessage());
				model.addAttribute("userForEdit", userConfirmRequestEntity);

				return "edit";
			}

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
	 * @param userEditRequestEntity 更新データ格納用エンティティ
	 * @param result バリデーション結果格納用クラス
	 * @param model Modelインスタンス
	 * @param redirectAttributes リダイレクト先へ値を渡すためのクラス
	 *
	 * @return ユーザ一覧画面
	 */
	@PostMapping(value = "/update")
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

		try {
			// ユーザ更新処理
			userService.updateUser(userEditRequestEntity);
		} catch (DataNotFoundException e) {
			log.error(e.getClass().getSimpleName(), e);
			List<String> errorList = new ArrayList<String>();
			errorList.add(e.getMessage());

			model.addAttribute("title", UserConstant.TITLE_UPDATE);
			model.addAttribute("validationError", errorList);
			model.addAttribute("userForEdit", userEditRequestEntity);

			return "edit";
		}

		redirectAttributes.addFlashAttribute("information", UserConstant.UPDATE_SUCCESS);

		// 更新後は一覧画面へリダイレクト
		return "redirect:/user/list/1";
	}

	/**
	 * ユーザ登録
	 *
	 * @param userEditRequestEntity 登録データ格納用エンティティ
	 * @param result バリデーション結果格納用クラス
	 * @param model Modelインスタンス
	 * @param redirectAttributes リダイレクト先へ値を渡すためのクラス
	 *
	 * @return ユーザ一覧画面
	 */
	@PostMapping(value = "/insert")
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

		// 登録後は一覧画面へリダイレクト
		return "redirect:/user/list/1";
	}

	/**
	 * ユーザ削除処理
	 *
	 * @param id 削除対象ID
	 * @param redirectAttributes リダイレクト先へ値を渡すためのクラス
	 *
	 * @return ユーザ一覧画面
	 */
	@PostMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		try {
			userService.deleteUser(id);
		} catch (DataNotFoundException e) {
			log.error(e.getClass().getSimpleName(), e);
			redirectAttributes.addFlashAttribute("exception", e.getMessage());

			// エラー時は一覧画面へ戻す
			return "redirect:/user/list/1";
		}

		redirectAttributes.addFlashAttribute("information", UserConstant.DELETE_SUCCESS);

		// 削除後は一覧画面へリダイレクト
		return "redirect:/user/list/1";
	}
}
