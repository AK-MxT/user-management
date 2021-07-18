package com.user.mng.controller;

import static org.hamcrest.MatcherAssert.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.user.mng.config.CsvDataSetLoader;
import com.user.mng.domain.model.entity.UserDetailEntity;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
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

	@Autowired
    Validator validator;

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

	/************************************************
	 * 一覧画面系テスト                             *
	 ************************************************/

	/**
	 * 未ログインでアクセス
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithAnonymousUser
	void 未ログインで一覧取得() throws Exception {

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(get("/user/list/1"))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * ログイン状態でアクセス
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
//	@WithUserDetails("user001")
//	@WithMockUser(username = "username", roles = {"USER"})
	void ログイン済で一覧取得() throws Exception {

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
			// csvで投入したデータが取得されていることを確認する
			// すべての項目の照合はし切れないのでIDだけチェック
			.andExpect(model().attribute("list", hasItem(hasProperty("id", is(1)))))
			.andExpect(model().attribute("list", hasItem(hasProperty("id", is(2)))))
			.andExpect(model().attribute("list", hasItem(hasProperty("id", is(3)))))
			.andExpect(view().name("list"));
	}

	/**
	 * バリデーションチェック（異常系）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー() throws Exception {

		UserListRequestEntity req = new UserListRequestEntity();
		req.setIdStart(1111111111);			// 10桁（エラー）
		req.setIdEnd(1111111111);			// 10桁（エラー）
		req.setLastName("aaaaaaaaaaa"); 	// 11文字（エラー）
		req.setFirstName("aaaaaaaaaaa"); 	// 11文字（エラー）
		req.setGender("2"); 				// 0: 男性・1: 女性（エラー）

		MvcResult mvcResult = this.mockmvc.perform(get("/user/list/1").flashAttr("userListRequestEntity", req).with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userListRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(5))
			.andExpect(status().isOk())
			.andExpect(view().name("list"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userListRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_idStart = result.getFieldError("idStart").getDefaultMessage();
		String actErr_idEnd = result.getFieldError("idEnd").getDefaultMessage();
		String actErr_lastName = result.getFieldError("lastName").getDefaultMessage();
		String actErr_firstName = result.getFieldError("firstName").getDefaultMessage();
		String actErr_gender = result.getFieldError("gender").getDefaultMessage();
		String expectErr_idStartEnd = "IDは9桁以下で入力してください";
		String expectErr_lastName = "姓は10文字以内で入力してください";
		String expectErr_firstName = "名は10文字以内で入力してください";
		String expectErr_gender = "性別の入力値に誤りがあります";

		// メッセージ照合処理
		assertThat(expectErr_idStartEnd, is(actErr_idStart));
		assertThat(expectErr_idStartEnd, is(actErr_idEnd));
		assertThat(expectErr_lastName, is(actErr_lastName));
		assertThat(expectErr_firstName, is(actErr_firstName));
		assertThat(expectErr_gender, is(actErr_gender));
	}

	/**
	 * バリデーションチェック（正常系）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー無し() throws Exception {

		UserListRequestEntity req = new UserListRequestEntity();
		req.setIdStart(111111111); // 9桁（正常）
		req.setIdEnd(111111111); // 9桁（正常）
		req.setLastName("テストテストテ"); // 10文字（正常）
		req.setFirstName("テストテストテ"); // 10文字（正常）
		req.setGender("0"); // 0: 男性（正常）

		this.mockmvc.perform(get("/user/list/1").flashAttr("userListRequestEntity", req).with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasNoErrors("userListRequestEntity"))
			.andExpect(status().isOk())
			.andExpect(view().name("list"));
	}

	/************************************************
	 * 詳細画面系テスト                             *
	 ************************************************/

	/**
	 * 未ログインでアクセス
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithAnonymousUser
	void 未ログインで詳細取得() throws Exception {

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(get("/user/detail/1"))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * ログイン状態でアクセス（正常系）
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
//	@WithUserDetails("user001")
//	@WithMockUser(username = "username", roles = {"USER"})
	void ログイン済で詳細取得() throws Exception {

		UserDetailResponseEntity result = new UserDetailResponseEntity();
		UserDetailEntity user = new UserDetailEntity();
		user.setId(1);
		user.setLastName("テスト");
		user.setFirstName("ユーザ01");
		user.setLastNameKana("テスト");
		user.setFirstNameKana("ユーザゼロイチ");
		user.setGender("男性");
		user.setBirthday("1997/10/20");
		user.setPostalCode("1234567");
		user.setAddress("東京都 A市 B町 1");
		user.setPhoneNumber("0000000000");
		user.setRemarks("test");
		user.setInsertUser("system");
		user.setInsertDate("2021/05/16 00:00:00");
		user.setUpdateUser("system");
		user.setUpdateDate("2021/06/29 21:58:50");
		user.setDeleteFlg("未削除");

		result.setUser(user);

		when(mockUserService.getUser(Long.valueOf("1"))).thenReturn(result);

		this.mockmvc.perform(get("/user/detail/1").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().isOk())
			.andExpect(model().attribute("userDetail", result.getUser()))
			.andExpect(view().name("detail"));
	}

	/**
	 * ログイン状態でアクセス（異常系：存在しないIDでアクセスし、一覧画面に戻る）
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
//	@WithUserDetails("user001")
//	@WithMockUser(username = "username", roles = {"USER"})
	void ログイン済で詳細取得_404() throws Exception {

		// TODO: 例外時の書き方調べる
//		when(mockUserService.getUser(Long.valueOf("4"))).thenThrow(new DataNotFoundException())

		this.mockmvc.perform(get("/user/detail/4").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().isOk())
			.andExpect(view().name("list"));
	}

	/************************************************
	 * 更新画面系テスト                             *
	 ************************************************/



	/************************************************
	 * 登録画面系テスト                             *
	 ************************************************/




	/************************************************
	 * 確認画面系テスト                             *
	 ************************************************/



}
