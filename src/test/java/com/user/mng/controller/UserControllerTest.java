package com.user.mng.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.user.mng.domain.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	// mockMvc TomcatサーバへデプロイすることなくHTTPリクエスト・レスポンスを扱うためのMockオブジェクト
	@Autowired
	private MockMvc mockmvc;

	@MockBean
	UserService mockUserService;

	@Autowired
	UserController target;

	@Before
	public void setup() {
		mockmvc = MockMvcBuilders.standaloneSetup(target).build();
	}

	@Test
	@WithAnonymousUser
	void ログインせずにアクセスするとリダイレクトされる() throws Exception {
		this.mockmvc.perform(get("/user/list/1")).andExpect(status().is3xxRedirection());
		//		.andExpect(view().name("login"));
	}

	@Test
	@WithUserDetails("user001")
	void ログイン後にアクセスすると正常終了() throws Exception {
		this.mockmvc.perform(get("/user/list/1")).andDo(print()).andExpect(status().isOk());
		//		.andExpect(view().name("login"));
	}
}
