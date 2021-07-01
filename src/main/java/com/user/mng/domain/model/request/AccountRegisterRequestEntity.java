package com.user.mng.domain.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * アカウント登録用リクエストエンティティ
 */
@Data
public class AccountRegisterRequestEntity {

	// ユーザID
	@NotEmpty(message = "ユーザIDは必須項目です")
	@Size(max = 20, message = "ユーザIDは20文字以内で入力してください")
	@Pattern(regexp = "^[0-9a-zA-Z]*$", message = "ユーザIDは半角英数字で入力してください")
	private String userName;

	// パスワード
	@NotEmpty(message = "パスワードは必須項目です")
	@Size(min = 8, max = 32, message = "パスワードは8文字以上32文字以内で入力してください")
	@Pattern(regexp = "^[0-9a-zA-Z]*$", message = "パスワードは半角英数字のみで入力してください")
	private String password;
}
