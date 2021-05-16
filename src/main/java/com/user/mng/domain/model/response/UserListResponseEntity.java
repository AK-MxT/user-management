package com.user.mng.domain.model.response;

import java.util.List;

import com.user.mng.domain.model.TrnUser;

import lombok.Data;

@Data
public class UserListResponseEntity {

	public List<TrnUser> userList;
}
