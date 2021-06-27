/**
 *
 */
package com.user.mng.domain.service;

import com.user.mng.domain.model.request.UserEditRequestEntity;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.exceptions.DataNotFoundException;

/**
 * ユーザ管理系サービス
 * 一覧、詳細、登録、更新、削除の各メソッドを定義
 */
public interface UserService {

	public UserListResponseEntity getUserList(Integer page, UserListRequestEntity userListRequestEntity);

	public UserDetailResponseEntity getUser(Long id) throws DataNotFoundException;

	public UserEditResponseEntity getUserForEdit(Long id);

	public void insertUser(UserEditRequestEntity user);

	public void updateUser(UserEditRequestEntity user);

	public void deleteUser(Long id);
}
