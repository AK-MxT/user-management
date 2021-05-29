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

	// ID
	private Integer id;

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

	// 郵便番号
	@NotEmpty(message = "郵便番号は必須項目です")
	@Pattern(regexp = "^\\d{7}", message = "郵便番号の入力値に誤りがあります")
	private String postalCode;

	// 都道府県
	@NotEmpty(message = "都道府県は必須項目です")
	private String prefecture;

	// 市区町村
	@NotEmpty(message = "市区町村は必須項目です")
	@Size(max = 30, message = "市区町村は30文字以内で入力してください")
	private String address1;

	// 町名
	@NotEmpty(message = "町名は必須項目です")
	@Size(max = 30, message = "町名は30文字以内で入力してください")
	private String address2;

	// 番地
	@Size(max = 30, message = "番地は30文字以内で入力してください")
	private String address3;

	// 建物名・部屋番号
	@Size(max = 30, message = "建物名・部屋番号は30文字以内で入力してください")
	private String address4;

	// 電話番号
	@NotEmpty(message = "電話番号は必須項目です")
	@Size(max = 11, message = "電話番号は11文字以内で入力してください")
	@Pattern(regexp = "[0-9]+", message = "電話番号の入力値に誤りがあります")
	private String phoneNumber;

	// 備考
	@Size(max = 400, message = "備考は400文字以内で入力してください")
	private String remarks;

	// 更新者
	@Size(max = 45, message = "更新者は45文字以内で入力してください")
	private String updateUser;
}
