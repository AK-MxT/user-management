package com.user.mng.domain.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.TrnUser;
import com.user.mng.domain.model.TrnUserExample;
import com.user.mng.domain.model.entity.UserDetailEntity;
import com.user.mng.domain.model.entity.UserListEntity;
import com.user.mng.domain.model.request.UserEditRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
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
	 * ユーザ取得処理（更新用のデータ取得）
	 *
	 * @param id ユーザID
	 *
	 * @return IDに紐づくユーザ1件
	 */
	@Override
	public UserEditResponseEntity getUserForEdit(Long id) {

		// ユーザの取得
		TrnUser result = trnUserMapper.selectByPrimaryKey(id.intValue());

		UserEditResponseEntity res = new UserEditResponseEntity();

		res.setId(result.getId());
		res.setLastName(result.getLastName());
		res.setFirstName(result.getFirstName());
		res.setLastNameKana(result.getLastNameKana());
		res.setFirstNameKana(result.getFirstNameKana());
		res.setGender(result.getGender());
		res.setBirthday(CommonUtils.formatToDate(result.getBirthday()));
		res.setPostalCode(result.getPostalCode());
		res.setPrefecture(result.getPrefecture());
		res.setAddress1(result.getAddress1());
		res.setAddress2(result.getAddress2());
		res.setAddress3(result.getAddress3());
		res.setAddress4(result.getAddress4());
		res.setPhoneNumber(result.getPhoneNumber());
		res.setRemarks(result.getRemarks());
		res.setInsertUser(result.getInsertUser());
		res.setInsertDate(CommonUtils.formatToDateTime(result.getInsertDate()));
		res.setUpdateUser(result.getUpdateUser());
		res.setUpdateDate(CommonUtils.formatToDateTime(result.getUpdateDate()));
		res.setDeleteFlg(result.getDeleteFlg() ? UserConstant.DELETE_FLG_TRUE : UserConstant.DELETE_FLG_FALSE);

		return res;
	}

	/**
	 * ユーザ更新処理
	 *
	 * @param id ユーザID
	 */
	@Override
	public void updateUser(UserEditRequestEntity user) {

		// システム日時
		Date now = new Date();

		TrnUserExample filter = new TrnUserExample();
		filter.createCriteria().andIdEqualTo(user.getId());

		// 更新値のセット
		TrnUser record = new TrnUser();
		record.setId(user.getId());
		record.setLastNameKana(user.getLastNameKana());
		record.setFirstNameKana(user.getFirstNameKana());
		record.setLastName(user.getLastName());
		record.setFirstName(user.getFirstName());
		record.setGender(user.getGender());
		record.setBirthday(CommonUtils.formatStrToDate(user.getBirthday()));
		record.setPostalCode(user.getPostalCode());
		record.setPrefecture(user.getPrefecture());
		record.setAddress1(user.getAddress1());
		record.setAddress2(user.getAddress2());
		record.setAddress3(user.getAddress3());
		record.setAddress4(user.getAddress4());
		record.setPhoneNumber(user.getPhoneNumber());
		record.setRemarks(user.getRemarks());
		record.setUpdateUser(user.getUpdateUser());
		record.setUpdateDate(now);

		// ユーザを更新
		int cnt = trnUserMapper.updateByExampleSelective(record, filter);

		// TODO 更新件数が1件でなければExceptionとする
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

	@Override
	public void insertUser(UserEditRequestEntity user) {

		// システム日時
		Date now = new Date();

		// 更新値のセット
		TrnUser record = new TrnUser();

		record.setLastNameKana(user.getLastNameKana());
		record.setFirstNameKana(user.getFirstNameKana());
		record.setLastName(user.getLastName());
		record.setFirstName(user.getFirstName());
		record.setGender(user.getGender());
		record.setBirthday(CommonUtils.formatStrToDate(user.getBirthday()));
		record.setPostalCode(user.getPostalCode());
		record.setPrefecture(user.getPrefecture());
		record.setAddress1(user.getAddress1());
		record.setAddress2(user.getAddress2());
		record.setAddress3(user.getAddress3());
		record.setAddress4(user.getAddress4());
		record.setPhoneNumber(user.getPhoneNumber());
		record.setRemarks(user.getRemarks());
		record.setInsertUser(
				StringUtils.isEmpty(user.getInsertUser()) ? UserConstant.DEFAULT_USERNAME : user.getInsertUser());
		record.setInsertDate(now);
		record.setUpdateUser(
				StringUtils.isEmpty(user.getInsertUser()) ? UserConstant.DEFAULT_USERNAME : user.getInsertUser());
		record.setUpdateDate(now);

		// ユーザを登録
		int cnt = trnUserMapper.insertSelective(record);
	}
}
