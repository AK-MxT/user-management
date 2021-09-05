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

	public static final String USERID_IS_EMPTY = "ユーザIDが入力されていません。";
	public static final String USERID_ALREADY_EXISTS = "このユーザIDは既に使用されています。";
	public static final String ACCOUNT_DUPLICATED = "アカウントが重複しています。管理者へ連絡してください。";
	public static final String ACCOUNT_LIMIT_EXCEEDED = "登録可能な最大アカウント件数に到達しています。システム管理者に連絡してください。";

	private AuthConstant() {
	}
}
