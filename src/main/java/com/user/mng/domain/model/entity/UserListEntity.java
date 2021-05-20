package com.user.mng.domain.model.entity;

import lombok.Data;

/**
 * ユーザ一覧返却用エンティティ
 * 一覧画面で表示する項目をセットする
 */

@Data
public class UserListEntity {

	// ID
	public Integer id;

	// 姓
	public String lastName;

	// 名
	public String firstName;

	// 性別
	public String gender;

	// 誕生日
	public String birthday;

	// 更新者
	public String updateUser;

	// 更新日
	public String updateDate;

	// 削除フラグ
	public String deleteFlg;
}
