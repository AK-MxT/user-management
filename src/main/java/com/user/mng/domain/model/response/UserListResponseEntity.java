package com.user.mng.domain.model.response;

import java.util.ArrayList;
import java.util.List;

import com.user.mng.domain.model.entity.UserListEntity;

import lombok.Data;

@Data
public class UserListResponseEntity {

	public List<UserListEntity> userList = new ArrayList<>();
}
