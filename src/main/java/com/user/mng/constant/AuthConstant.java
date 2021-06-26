package com.user.mng.constant;

/**
 * 認証サービスまわりの定数クラス
 */
public final class AuthConstant {

	// ロール
	public static final String ROLE_USER = "USER";
	public static final String ROLE_ADMIN = "ADMIN";

	// アラートメッセージ系
	public static final String REGISTER_SUCCESS = "アカウントの登録が完了しました。";
	public static final String LOGIN_ERROR = "ユーザ名 または パスワードに誤りがあります。";

	private AuthConstant() {
	}
}
