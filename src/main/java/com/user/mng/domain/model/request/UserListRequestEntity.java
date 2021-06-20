package com.user.mng.domain.model.request;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * ユーザ検索用リクエストエンティティ
 */
@Data
public class UserListRequestEntity {

	// ID（開始）
	@Digits(integer = 9, fraction = 0, message = "IDは9桁以下で入力してください")
	private Integer idStart;

	// ID（終了）
	@Digits(integer = 9, fraction = 0, message = "IDは9桁以下で入力してください")
	private Integer idEnd;

	// 姓
	@Size(max = 10, message = "姓は10文字以内で入力してください")
	private String lastName;

	// 名
	@Size(max = 10, message = "名は10文字以内で入力してください")
	private String firstName;

	// 性別
	@Pattern(regexp = "[0-1]")
	private String gender;

	// 取得件数
	private Integer limit;

	// ページング件数
	private Integer paging;

	// オフセット
	private Integer offset;
}
