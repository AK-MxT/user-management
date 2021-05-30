/**
 *
 */
package com.user.mng.domain.service;

import com.user.mng.domain.model.request.UserUpdateRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;

/**
 * ユーザ管理系サービス
 * 一覧、詳細、登録、更新、削除の各メソッドを定義
 */
public interface UserService {

	public UserListResponseEntity getUserList();

	public UserDetailResponseEntity getUser(Long id);

	public UserEditResponseEntity getUserForEdit(Long id);

	public void updateUser(UserUpdateRequestEntity user);

	public void deleteUser(Long id);
}
