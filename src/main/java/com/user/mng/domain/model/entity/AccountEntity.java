package com.user.mng.domain.model.entity;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.user.mng.constant.AuthConstant;
import com.user.mng.domain.model.TrnAccount;

/**
 * Spring Security用のエンティティ
 * 認証で利用するアカウントデータを扱う
 */
public class AccountEntity implements UserDetails {

	// アカウントテーブル
	private TrnAccount account;

	private Collection<GrantedAuthority> authorities;

	public AccountEntity(TrnAccount account) {
		this.account = account;
		this.authorities = new ArrayList<>();
		this.authorities.add(
				new SimpleGrantedAuthority(account.getAdminFlg() ? AuthConstant.ROLE_ADMIN : AuthConstant.ROLE_USER));
	}

	public String getId() {
		return account.getId().toString();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getUsername() {
		return account.getUserName();
	}

	@Override
	public String getPassword() {
		return account.getPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
		// 利用しないのでtrue固定
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// 利用しないのでtrue固定
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 利用しないのでtrue固定
		return true;
	}

	@Override
	public boolean isEnabled() {
		// 削除フラグで有効 / 無効を判断
		return !account.getDeleteFlg();
	}

}
