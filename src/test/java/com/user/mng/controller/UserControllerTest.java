package com.user.mng.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.user.mng.UserMngMockApplication;
import com.user.mng.config.CsvDataSetLoader;
import com.user.mng.domain.service.UserService;

@AutoConfigureMockMvc
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class
})
@SpringBootTest(classes = {UserMngMockApplication.class})
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
		mockmvc = MockMvcBuilders.standaloneSetup(target).apply(springSecurity()).build();
	}

	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithAnonymousUser
	void ログインせずにアクセスするとリダイレクトされる() throws Exception {
		this.mockmvc.perform(get("/user/list/1")).andExpect(status().is3xxRedirection());
		//		.andExpect(view().name("login"));
	}

	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithUserDetails(value = "user001", userDetailsServiceBeanName = "hogehoge")
	void ログイン後にアクセスすると正常終了() throws Exception {
		this.mockmvc.perform(get("/user/list/1")).andDo(print()).andExpect(status().isOk());
		//		.andExpect(view().name("login"));
	}
}
