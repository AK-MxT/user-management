package com.user.mng.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.user.mng.config.CsvDataSetLoader;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserListResponseEntity;
import com.user.mng.domain.service.UserService;

@AutoConfigureMockMvc
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class
})
@SpringBootTest
class UserControllerTest {

	// mockMvc TomcatサーバへデプロイすることなくHTTPリクエスト・レスポンスを扱うためのMockオブジェクト
	@Autowired
	private MockMvc mockmvc;

	@Mock
	UserService mockUserService;

	@InjectMocks
	UserController userController;

	@BeforeEach
	public void setup() {
        // 各テストの実行前にモックオブジェクトを初期化する
        MockitoAnnotations.openMocks(this);
		this.mockmvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

//	@Test
//	@DatabaseSetup("/data/")
//	@Transactional
//	@WithMockUser
////	@WithAnonymousUser
//	void ログインせずにアクセスするとリダイレクトされる() throws Exception {
//		this.mockmvc.perform(get("/user/list/1")).andExpect(status().is3xxRedirection());
//		//		.andExpect(view().name("login"));
//	}

	@Test
	@DatabaseSetup("/data/")
	@Transactional
//	@WithUserDetails("user001")
//	@WithMockUser(username = "username", roles = {"USER"})
	void ログイン後にアクセスすると正常終了() throws Exception {

		UserListResponseEntity list = new UserListResponseEntity();
		when(mockUserService.getUserList(1, new UserListRequestEntity())).thenReturn(list);

		this.mockmvc.perform(get("/user/list/1").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attribute("page", 1))
//			.andExpect(model().attribute("searchItems", req))
			.andExpect(model().attribute("list", list.getUserList()))
			.andExpect(view().name("list"));
	}
}
