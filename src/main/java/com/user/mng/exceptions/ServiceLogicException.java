package com.user.mng.exceptions;

/**
 * 業務ロジックに関する例外を管理する
 */
public class ServiceLogicException extends RuntimeException {

	public ServiceLogicException(String message) {
		super(message);
	}
}
