package com.user.mng.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.user.mng.constant.AuthConstant;
import com.user.mng.domain.model.TrnAccount;
import com.user.mng.domain.model.TrnAccountExample;
import com.user.mng.domain.model.entity.AccountEntity;
import com.user.mng.domain.repository.TrnAccountMapper;

@Service
public class LoginServiceImpl implements UserDetailsService {

	private final TrnAccountMapper trnAccountMapper;

	public LoginServiceImpl(TrnAccountMapper trnAccountMapper) {
		this.trnAccountMapper = trnAccountMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (StringUtils.isEmpty(username)) {
			throw new UsernameNotFoundException("username is empty.");
		}

		// 検索条件
		TrnAccountExample filter = new TrnAccountExample();
		filter.createCriteria().andUserNameEqualTo(username).andDeleteFlgEqualTo(Boolean.FALSE);

		List<TrnAccount> accountList = trnAccountMapper.selectByExample(filter);

		if (accountList.size() != 1) {
			// 取得結果が1件でない場合、エラー
			throw new UsernameNotFoundException("username is not unique.");
		}

		// 取得したユーザをセット
		TrnAccount account = accountList.get(0);

		// 権限の設定
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(
				new SimpleGrantedAuthority(account.getAdminFlg() ? AuthConstant.ROLE_ADMIN : AuthConstant.ROLE_USER));

		return new AccountEntity(account);
	}
}
