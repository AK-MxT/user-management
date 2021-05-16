package com.user.mng.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.user.mng.domain.model.TrnUser;
import com.user.mng.domain.model.TrnUserExample;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.repository.TrnUserMapper;

@Service
public class UserServiceImpl implements UserService {

	private final TrnUserMapper trnUserMapper;

	public UserServiceImpl(TrnUserMapper trnUserMapper) {
		this.trnUserMapper = trnUserMapper;
	}

	/**
	 * ユーザリスト取得処理（ユーザ一覧画面）
	 *
	 * @return ユーザリスト
	 */
	@Override
	public UserListResponseEntity getUserList() {

		TrnUserExample filter = new TrnUserExample();
		filter.setOrderByClause("id");

		List<TrnUser> result = trnUserMapper.selectByExample(filter);

		UserListResponseEntity res = new UserListResponseEntity();
		res.setUserList(result);

		return res;
	}

	/**
	 * ユーザ取得処理（ユーザ詳細画面）
	 *
	 * @param id ユーザID
	 *
	 * @return IDに紐づくユーザ1件
	 */
	@Override
	public UserDetailResponseEntity getUser(Long id) {

		TrnUser result = trnUserMapper.selectByPrimaryKey(id.intValue());

		UserDetailResponseEntity res = new UserDetailResponseEntity();
		res.setUser(result);

		return res;
	}
}
