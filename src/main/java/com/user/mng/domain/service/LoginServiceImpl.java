package com.user.mng.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.user.mng.constant.AuthConstant;
import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.TrnAccount;
import com.user.mng.domain.model.TrnAccountExample;
import com.user.mng.domain.model.entity.AccountEntity;
import com.user.mng.domain.repository.TrnAccountMapper;
import com.user.mng.exceptions.ServiceLogicException;

/**
 * ログイン管理サービス
 * SpringSecurityが用意しているUserDetailsServiceの実装クラス
 * ログイン管理をDBで行いたい場合に必要
 */
@Service
public class LoginServiceImpl implements UserDetailsService {

	// アカウント管理テーブルのマッパー
	private final TrnAccountMapper trnAccountMapper;

	public LoginServiceImpl(TrnAccountMapper trnAccountMapper) {
		this.trnAccountMapper = trnAccountMapper;
	}

	/**
	 * ログイン認証処理
	 *
	 * @param userName ユーザID
	 *
	 * @return アカウント情報
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		if (StringUtils.isEmpty(userName)) {
			throw new UsernameNotFoundException(AuthConstant.USERID_IS_EMPTY);
		}

		// 検索条件
		TrnAccountExample filter = new TrnAccountExample();
		filter.createCriteria().andUserNameEqualTo(userName).andDeleteFlgEqualTo(Boolean.FALSE);

		List<TrnAccount> accountList = trnAccountMapper.selectByExample(filter);

		if (accountList.size() != 1) {
			// 取得結果が1件でない場合、エラー
			throw new UsernameNotFoundException(AuthConstant.ACCOUNT_DUPLICATED);
		}

		// 取得したユーザをセット
		TrnAccount account = accountList.get(0);

		// 権限の設定
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(
				new SimpleGrantedAuthority(account.getAdminFlg() ? AuthConstant.ROLE_ADMIN : AuthConstant.ROLE_USER));

		return new AccountEntity(account);
	}

	/**
	 * アカウント登録処理
	 *
	 * @param userName
	 * @param password
	 */
	@Transactional
	public void registerAccount(String userName, String password) {

		// 検索条件
		TrnAccountExample filter = new TrnAccountExample();
		filter.createCriteria().andUserNameEqualTo(userName).andDeleteFlgEqualTo(Boolean.FALSE);

		List<TrnAccount> accountList = trnAccountMapper.selectByExample(filter);

		if (accountList.size() != 0) {
			// 取得結果が0件でない場合、既に使われているユーザ名のため、エラー
			throw new ServiceLogicException(AuthConstant.USERID_ALREADY_EXISTS);
		}

		// システム日時
		Date now = new Date();

		TrnAccount record = new TrnAccount();

		record.setUserName(userName);
		record.setPassword(password);
		record.setAdminFlg(Boolean.FALSE);
		record.setInsertDate(now);
		record.setInsertUser(UserConstant.DEFAULT_USERNAME);
		record.setUpdateDate(now);
		record.setUpdateUser(UserConstant.DEFAULT_USERNAME);

		trnAccountMapper.insertSelective(record);
	}
}
