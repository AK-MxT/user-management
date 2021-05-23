/**
 *
 */
package com.user.mng.domain.service;

import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;

/**
 * @author A.K
 *
 */
public interface UserService {

	public UserListResponseEntity getUserList();

	public UserDetailResponseEntity getUser(Long id);

	public void deleteUser(Long id);
}
