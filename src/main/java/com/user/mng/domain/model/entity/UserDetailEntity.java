package com.user.mng.domain.model.entity;

import lombok.Data;

/**
 * ユーザ詳細返却用エンティティ
 * ユーザ詳細画面で表示する項目をセットする
 */

@Data
public class UserDetailEntity {

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

	// 住所
	public String address;

	// 電話番号
	public String phoneNumber;

	// 備考
	public String remarks;

	// 作成者
	public String insertUser;

	// 作成日
	public String insertDate;

	// 更新者
	public String updateUser;

	// 更新日
	public String updateDate;

	// 削除フラグ
	public String deleteFlg;
}
