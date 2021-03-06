package com.user.mng.domain.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.TrnUser;
import com.user.mng.domain.model.TrnUserExample;
import com.user.mng.domain.model.entity.UserDetailEntity;
import com.user.mng.domain.model.entity.UserListEntity;
import com.user.mng.domain.model.request.UserEditRequestEntity;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.repository.TrnUserMapper;
import com.user.mng.exceptions.DataNotFoundException;
import com.user.mng.exceptions.ServiceLogicException;
import com.user.mng.utils.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	// 一覧画面の1ページあたりの表示件数
	@Value("${db.user.list.limit}")
	private int selectLimit;

	// DBへの登録制限（大量登録などの攻撃対策。実サービスに乗せる場合には不要）
	@Value("${db.restrict.user}")
	private int userLimit;

	// ユーザ情報テーブルのマッパー
	private final TrnUserMapper trnUserMapper;

	// 検索条件の区分値
	private static final String START = "0";
	private static final String END = "1";
	private static final String BOTH = "2";
	private static final String NONE = "3";

	public UserServiceImpl(TrnUserMapper trnUserMapper) {
		this.trnUserMapper = trnUserMapper;
	}

	/**
	 * ユーザリスト取得処理（ユーザ一覧画面）
	 *
	 * @param page 現在のページ数
	 * @param userListRequestEntity 検索条件
	 *
	 * @return ユーザデータのリスト
	 */
	@Override
	public UserListResponseEntity getUserList(Integer page, UserListRequestEntity userListRequestEntity) {

		// IDの検索条件指定状態
		String idStatus = this.checkId(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd());

		// 姓名の検索条件指定状態
		String nameStatus = this.checkNames(userListRequestEntity.getLastName(), userListRequestEntity.getFirstName());

		// 性別の検索条件指定状態
		String genderStatus = this.checkGender(userListRequestEntity.getGender());

		// 検索条件
		TrnUserExample filter = new TrnUserExample();

		// 検索条件の設定
		switch (idStatus) {
		case START:
			switch (nameStatus) {
			case START:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()));
				break;
			case END:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case BOTH:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case NONE:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart())
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdGreaterThanOrEqualTo(userListRequestEntity.getIdStart());
				break;
			}
			break;
		case END:
			switch (nameStatus) {
			case START:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()));
				break;
			case END:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case BOTH:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case NONE:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd())
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdLessThanOrEqualTo(userListRequestEntity.getIdEnd());
				break;
			}
			break;
		case BOTH:
			switch (nameStatus) {
			case START:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria()
						.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()));
				break;
			case END:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria()
						.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case BOTH:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria()
						.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case NONE:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andIdBetween(userListRequestEntity.getIdStart(), userListRequestEntity.getIdEnd())
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andIdBetween(userListRequestEntity.getIdStart(),
						userListRequestEntity.getIdEnd());
				break;
			}
			break;
		case NONE:
			switch (nameStatus) {
			case START:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria().andLastNameLike("%" + userListRequestEntity.getLastName() + "%");
				break;
			case END:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria()
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case BOTH:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria()
							.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
							.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()))
							.andGenderEqualTo(genderStatus);
					break;
				}
				filter.createCriteria()
						.andLastNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getLastName()))
						.andFirstNameLike(CommonUtils.generateLikeFormat(userListRequestEntity.getFirstName()));
				break;
			case NONE:
				switch (genderStatus) {
				case UserConstant.GENDER_MAN:
				case UserConstant.GENDER_WOMAN:
					filter.createCriteria().andGenderEqualTo(genderStatus);
					break;
				}
				break;
			}
			break;
		}

		// ソートの設定
		filter.setOrderByClause("id");

		// pageの値を元にoffsetを算出する
		int offset = page == 1 ? 0 : --page * selectLimit;

		// offset, limitの設定
		RowBounds hoge = new RowBounds(offset, selectLimit);

		// 検索結果の総数を取得
		Long cnt = trnUserMapper.countByExample(filter);

		// ユーザ取得処理
		List<TrnUser> result = trnUserMapper.selectByExampleWithRowbounds(filter, hoge);

		// ページング用の件数を計算
		Double pageCount = Math.ceil(cnt / (double) selectLimit);

		// ページング件数をセット
		userListRequestEntity.setPaging(pageCount.intValue());

		// 返却用エンティティを生成
		UserListResponseEntity res = new UserListResponseEntity();

		result.forEach(p -> {
			UserListEntity user = new UserListEntity();

			// 検索結果のセット
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
	 * IDの検索条件の指定状態をチェックする
	 *
	 * @param idStart IDの始点
	 * @param idEnd IDの終点
	 *
	 * @return 指定状態の区分値（0: 始点だけ指定 / 1: 終点だけ指定 / 2: 両方指定 / 3: 指定なし）
	 */
	private String checkId(Integer idStart, Integer idEnd) {
		if (Objects.isNull(idStart)) {
			if (Objects.isNull(idEnd)) {
				return NONE;
			}
			return END;
		} else if (Objects.isNull(idEnd)) {
			return START;
		}
		return BOTH;
	}

	/**
	 * 名前の検索条件の指定状態をチェックする
	 *
	 * @param lastName 姓
	 * @param firstName 名
	 *
	 * @return 指定状態の区分値（0: 姓だけ指定 / 1: 名だけ指定 / 2: 両方指定 / 3: 指定なし）
	 */
	private String checkNames(String lastName, String firstName) {
		if (StringUtils.isBlank(lastName)) {
			if (StringUtils.isBlank(firstName)) {
				return NONE;
			}
			return END;
		} else if (StringUtils.isBlank(firstName)) {
			return START;
		}
		return BOTH;
	}

	/**
	 * 性別の検索条件の指定状態をチェックする
	 *
	 * @param gender 性別
	 *
	 * @return 指定がなければ「3 (未指定)」、あればgenderをそのまま返却
	 */
	private String checkGender(String gender) {
		return StringUtils.isBlank(gender) ? NONE : gender;
	}

	/**
	 * ユーザ取得処理（ユーザ詳細画面）
	 *
	 * @param id ユーザID
	 * @throws DataNotFoundException
	 *
	 * @return IDに紐づくユーザ1件
	 */
	@Override
	public UserDetailResponseEntity getUser(Long id) throws DataNotFoundException {

		// IDに紐づくユーザを取得する
		TrnUser result = trnUserMapper.selectByPrimaryKey(id.intValue());

		if (Objects.isNull(result)) {
			// 取得結果が0件の場合、エラー
			throw new DataNotFoundException(UserConstant.GET_USER_NOT_FOUND + id.toString());
		}

		// レスポンス用のエンティティを生成
		UserDetailResponseEntity res = new UserDetailResponseEntity();
		UserDetailEntity detail = new UserDetailEntity();

		// 取得結果をセット
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
	 * @throws DataNotFoundException
	 *
	 * @return IDに紐づくユーザ1件
	 */
	@Override
	public UserEditResponseEntity getUserForEdit(Long id) throws DataNotFoundException {

		// ユーザの取得
		TrnUser result = trnUserMapper.selectByPrimaryKey(id.intValue());

		if (Objects.isNull(result)) {
			// 取得結果が0件の場合、エラー
			throw new DataNotFoundException(UserConstant.GET_USER_NOT_FOUND + id.toString());
		}

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
	 * ユーザ件数確認
	 * DBに登録されているユーザの件数を確認する
	 * 設定ファイルで規定している件数が既に登録されている場合、エラー
	 *
	 */
	@Override
	public void checkUserCount() throws ServiceLogicException {

		// trn_userテーブルの件数取得
		long cnt = trnUserMapper.countByExample(new TrnUserExample());

		if (cnt >= userLimit) {
			// DBのデータ件数が規定の件数以上の場合、登録不可とする
			throw new ServiceLogicException(UserConstant.USER_LIMIT_EXCEEDED);
		}
	}

	/**
	 * ユーザ更新処理
	 *
	 * @param user 更新するユーザのデータ
	 * @throws DataNotFoundException
	 */
	@Override
	@Transactional
	public void updateUser(UserEditRequestEntity user) throws DataNotFoundException {

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

		if (cnt != 1) {
			// 更新件数が1件でない場合、エラー
			throw new DataNotFoundException(UserConstant.UPDATE_USER_NOT_FOUND + user.getId().toString());
		}

		log.info(UserConstant.UPDATE_SUCCESS + UserConstant.ID + user.getId());
	}

	/**
	 * ユーザ削除処理
	 *
	 * @param id ユーザID
	 * @throws DataNotFoundException
	 */
	@Override
	@Transactional
	public void deleteUser(Long id) throws DataNotFoundException {

		// ユーザの削除
		int cnt = trnUserMapper.deleteByPrimaryKey(id.intValue());

		if (cnt != 1) {
			// 削除件数が1件でない場合、エラー
			throw new DataNotFoundException(UserConstant.DELETE_USER_NOT_FOUND + id.toString());
		}

		log.info(UserConstant.DELETE_SUCCESS + UserConstant.ID + id);
	}

	/**
	 * ユーザ登録処理
	 *
	 * @param user 登録するユーザのデータ
	 */
	@Override
	@Transactional
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
		trnUserMapper.insertSelective(record);

		log.info(UserConstant.REGISTER_SUCCESS);
	}
}
