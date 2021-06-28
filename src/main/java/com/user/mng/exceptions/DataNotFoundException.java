package com.user.mng.exceptions;

/**
 * DB処理において対象のデータが存在しない場合の例外を管理する
 */
public class DataNotFoundException extends RuntimeException {

	public DataNotFoundException(String message) {
		super(message);
	}
}
