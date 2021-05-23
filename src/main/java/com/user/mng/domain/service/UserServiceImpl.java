package com.user.mng.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.TrnUser;
import com.user.mng.domain.model.TrnUserExample;
import com.user.mng.domain.model.entity.UserDetailEntity;
import com.user.mng.domain.model.entity.UserListEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.repository.TrnUserMapper;
import com.user.mng.utils.CommonUtils;

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

		// ユーザ取得処理
		List<TrnUser> result = trnUserMapper.selectByExample(filter);

		// 返却用エンティティを生成
		UserListResponseEntity res = new UserListResponseEntity();

		result.forEach(p -> {
			UserListEntity user = new UserListEntity();
			user.setId(p.getId());
			user.setLastName(p.getLastName());
			user.setFirstName(p.getFirstName());
			user.setGender(UserConstant.GENDER_MAN.equals(p.getGender()) ? UserConstant.GENDER_MAN_VIEW
					: UserConstant.GENDER_WOMAN_VIEW);
			user.setBirthday(CommonUtils.formatToDate(p.getBirthday()));
			user.setUpdateUser(p.getUpdateUser());
			user.setUpdateDate(CommonUtils.formatToDateTime(p.getUpdateDate()));
			user.setDeleteFlg(p.getDeleteFlg() ? UserConstant.DELETE_FLG_TRUE : UserConstant.DELETE_FLG_FALSE);

			res.getUserList().add(user);
		});

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
		UserDetailEntity detail = new UserDetailEntity();

		detail.setId(result.getId());
		detail.setLastName(result.getLastName());
		detail.setFirstName(result.getFirstName());
		detail.setLastNameKana(result.getLastNameKana());
		detail.setFirstNameKana(result.getFirstNameKana());
		detail.setGender(UserConstant.GENDER_MAN.equals(result.getGender()) ? UserConstant.GENDER_MAN_VIEW
				: UserConstant.GENDER_WOMAN_VIEW);
		detail.setBirthday(CommonUtils.formatToDate(result.getBirthday()));
		detail.setPostalCode(result.getPostalCode());
		detail.setAddress(CommonUtils.formatAddress(result.getPrefecture(), result.getAddress1(), result.getAddress2(),
				result.getAddress3(), result.getAddress4()));
		detail.setPhoneNumber(result.getPhoneNumber());
		detail.setRemarks(result.getRemarks());
		detail.setInsertUser(result.getInsertUser());
		detail.setInsertDate(CommonUtils.formatToDateTime(result.getInsertDate()));
		detail.setUpdateUser(result.getUpdateUser());
		detail.setUpdateDate(CommonUtils.formatToDateTime(result.getUpdateDate()));
		detail.setDeleteFlg(result.getDeleteFlg() ? UserConstant.DELETE_FLG_TRUE : UserConstant.DELETE_FLG_FALSE);

		res.setUser(detail);

		return res;
	}

	/**
	 * ユーザ削除処理
	 *
	 * @param id ユーザID
	 */
	@Override
	public void deleteUser(Long id) {

		// TODO 事前に対象ユーザが存在するかチェック

		// ユーザの削除
		trnUserMapper.deleteByPrimaryKey(id.intValue());

		// TODO 削除件数が0件だったら例外とする
	}
}
