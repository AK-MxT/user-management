package com.user.mng.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

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


	@Autowired
	WebApplicationContext wac;

	// TomcatサーバへデプロイすることなくHTTPリクエスト・レスポンスを扱うためのMockオブジェクト
	@Autowired
	private MockMvc mockmvc;

	// テスト対象のControllerが依存しているServiceクラス
	@Mock
	UserService mockUserService;

	// テスト対象のController
	@InjectMocks
	UserController userController;

	@BeforeEach
	public void setup() {
        // 各テストの実行前にモックオブジェクトを初期化する
        MockitoAnnotations.openMocks(this);
		this.mockmvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
	}

	/**
	 * 一覧画面系テスト
	 */

	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithAnonymousUser
	void 未ログインで一覧取得() throws Exception {

		UserListResponseEntity list = new UserListResponseEntity();
		UserListRequestEntity req = new UserListRequestEntity();
		when(mockUserService.getUserList(1, req)).thenReturn(list);

		this.mockmvc.perform(get("/user/list/1"))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@DatabaseSetup("/data/")
	@Transactional
//	@WithUserDetails("user001")
//	@WithMockUser(username = "username", roles = {"USER"})
	void getListログイン済() throws Exception {

		UserListResponseEntity list = new UserListResponseEntity();
		UserListRequestEntity req = new UserListRequestEntity();
		req.setPaging(1);
		when(mockUserService.getUserList(1, req)).thenReturn(list);

		this.mockmvc.perform(get("/user/list/1").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().isOk())
			.andExpect(model().attribute("page", 1))
			.andExpect(model().attribute("searchItems", req))
			.andExpect(model().attribute("list", hasItem(hasProperty("id", is(1)))))
			.andExpect(model().attribute("list", hasItem(hasProperty("id", is(2)))))
			.andExpect(model().attribute("list", hasItem(hasProperty("id", is(3)))))
			.andExpect(view().name("list"));
	}

	/**
	 * 詳細画面系テスト
	 */



	/**
	 * 更新画面系テスト
	 */



	/**
	 * 登録画面系テスト
	 */




	/**
	 * 確認画面系テスト
	 */



}
