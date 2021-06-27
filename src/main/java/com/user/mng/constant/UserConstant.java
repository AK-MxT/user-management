package com.user.mng.constant;

/**
 * Userサービスまわりの定数クラス
 */
public final class UserConstant {

	// 性別
	public static final String GENDER_MAN = "0";
	public static final String GENDER_WOMAN = "1";
	public static final String GENDER_MAN_VIEW = "男性";
	public static final String GENDER_WOMAN_VIEW = "女性";

	// 削除フラグ
	public static final String DELETE_FLG_FALSE = "未削除";
	public static final String DELETE_FLG_TRUE = "削除済";

	// 登録者・更新者のデフォルト値
	public static final String DEFAULT_USERNAME = "system";

	// 可変Title系
	public static final String TITLE_REGISTER = "ユーザ登録";
	public static final String TITLE_UPDATE = "ユーザ更新";
	public static final String TITLE_REGISTER_CONFIRM = "ユーザ登録確認";
	public static final String TITLE_UPDATE_CONFIRM = "ユーザ更新確認";

	// アラートメッセージ系
	public static final String REGISTER_SUCCESS = "ユーザの登録が完了しました。";
	public static final String UPDATE_SUCCESS = "ユーザの更新が完了しました。";
	public static final String DELETE_SUCCESS = "ユーザを削除しました。";

	public static final String GET_USER_NOT_FOUND = "取得対象のユーザが存在しません。ID：";
	public static final String UPDATE_USER_NOT_FOUND = "ユーザの更新に失敗しました。ID：";
	public static final String DELETE_USER_NOT_FOUND = "削除対象のユーザが存在しません。ID：";
	public static final String INSERT_FAILED = "ユーザの登録に失敗しました。";

	// DB関連
	public static final int DEFAULT_LIMIT = 10;

	private UserConstant() {
	}
}
