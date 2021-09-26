package com.user.mng.domain.model.response;

import lombok.Data;

@Data
public class UserEditResponseEntity {

	// ID
	public Integer id;

	// 姓
	public String lastName;

	// 名
	public String firstName;

	// 姓カナ
	public String lastNameKana;

	// 名カナ
	public String firstNameKana;

	// 性別
	public String gender;

	// 誕生日
	public String birthday;

	// 郵便番号
	public String postalCode;

	// 都道府県
	public String prefecture;

	// 市区町村
	public String address1;

	// 町名
	public String address2;

	// 番地
	public String address3;

	// 建物名・部屋番号
	public String address4;

	// 電話番号
	public String phoneNumber;

	// 備考
	public String remarks;

	// 登録者
	public String insertUser;

	// 登録日時
	public String insertDate;

	// 更新者
	public String updateUser;

	// 更新日時
	public String updateDate;

	// 削除フラグ
	public String deleteFlg;
}
