package com.user.mng.domain.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * ユーザ登録更新時確認用リクエストエンティティ
 */
@Data
public class UserConfirmRequestEntity {

	// 姓カナ
	@NotEmpty(message = "姓カナは必須項目です")
	@Size(max = 20, message = "姓カナは20文字以内で入力してください")
	private String lastNameKana;

	// 名カナ
	@NotEmpty(message = "名カナは必須項目です")
	@Size(max = 20, message = "名カナは20文字以内で入力してください")
	private String firstNameKana;

	// 姓
	@NotEmpty(message = "姓は必須項目です")
	@Size(max = 10, message = "姓は10文字以内で入力してください")
	private String lastName;

	// 名
	@NotEmpty(message = "名は必須項目です")
	@Size(max = 10, message = "名は10文字以内で入力してください")
	private String firstName;

	// 性別
	@NotEmpty(message = "性別は必須項目です")
	@Pattern(regexp = "[0-1]")
	private String gender;
}
