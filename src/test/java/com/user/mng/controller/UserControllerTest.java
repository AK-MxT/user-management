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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.user.mng.config.CsvDataSetLoader;
import com.user.mng.constant.UserConstant;
import com.user.mng.domain.model.entity.UserDetailEntity;
import com.user.mng.domain.model.request.UserConfirmRequestEntity;
import com.user.mng.domain.model.request.UserEditRequestEntity;
import com.user.mng.domain.model.request.UserListRequestEntity;
import com.user.mng.domain.model.response.UserDetailResponseEntity;
import com.user.mng.domain.model.response.UserEditResponseEntity;
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
	void バリデーションエラー_一覧取得() throws Exception {

		UserListRequestEntity req = new UserListRequestEntity();
		req.setIdStart(1111111111);			// 10桁（エラー）
		req.setIdEnd(1111111111);			// 10桁（エラー）
		req.setLastName("aaaaaaaaaaa"); 	// 11文字（エラー）
		req.setFirstName("aaaaaaaaaaa"); 	// 11文字（エラー）
		req.setGender("2"); 				// 0: 男性・1: 女性（エラー）

		MvcResult mvcResult = this.mockmvc.perform(get("/user/list/1")
				.flashAttr("userListRequestEntity", req)
				.with(user("username").roles("USER")))
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
		req.setIdStart(111111111); 			// 9桁（正常）
		req.setIdEnd(111111111); 			// 9桁（正常）
		req.setLastName("テストテストテ"); 	// 10文字（正常）
		req.setFirstName("テストテストテ"); // 10文字（正常）
		req.setGender("0"); 				// 0: 男性（正常）

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
	void ログイン済で詳細取得_データなしでリダイレクト() throws Exception {

		String expectedException = "取得対象のユーザが存在しません。ID：4";

		this.mockmvc.perform(get("/user/detail/4").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("exception", expectedException))
			.andExpect(redirectedUrl("/user/list/1"));
	}

	/************************************************
	 * 更新画面系テスト                             *
	 ************************************************/

	/**
	 * 未ログインでアクセス(GET)
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithAnonymousUser
	void 未ログインで更新用データ取得_GET() throws Exception {

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(get("/user/edit/1"))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * 未ログインでアクセス(POST)
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
	@WithAnonymousUser
	void 未ログインで更新用データ取得_POST() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		// 必須項目だけセット
		req.setLastName("テスト");
		req.setLastNameKana("テスト");
		req.setFirstName("ユーザ01");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("1");
		req.setBirthday("2001/10/03");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(post("/user/edit/1")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * ログイン状態でアクセス（正常系・GET）
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
	void ログイン済で更新用データ取得_GET() throws Exception {

		UserEditResponseEntity result = new UserEditResponseEntity();
		result.setId(1);
		result.setLastName("テスト");
		result.setFirstName("ユーザ01");
		result.setLastNameKana("テスト");
		result.setFirstNameKana("ユーザゼロイチ");
		result.setGender("0");
		result.setBirthday("1997/10/20");
		result.setPostalCode("1234567");
		result.setPrefecture("東京都");
		result.setAddress1("A市");
		result.setAddress2("B町");
		result.setAddress3("1");
		result.setPhoneNumber("0000000000");
		result.setRemarks("test");
		result.setInsertUser("system");
		result.setInsertDate("2021/05/16 00:00:00");
		result.setUpdateUser("system");
		result.setUpdateDate("2021/06/29 21:58:50");
		result.setDeleteFlg("未削除");

		when(mockUserService.getUserForEdit(Long.valueOf("1"))).thenReturn(result);

		this.mockmvc.perform(get("/user/edit/1").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_UPDATE))
			.andExpect(model().attribute("userForEdit", result))
			.andExpect(view().name("edit"));
	}

	/**
	 * ログイン状態でアクセス（正常系・POST）
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@Transactional
	void ログイン済で更新用データ取得_POST() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		req.setId(1);
		req.setLastName("テスト");
		req.setFirstName("ユーザ01");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("0");
		req.setBirthday("1997/10/20");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("1");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		this.mockmvc.perform(post("/user/edit/1")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_UPDATE))
			.andExpect(model().attribute("userForEdit", req))
			.andExpect(view().name("edit"));
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
	void ログイン済で更新用データ取得_データなしでリダイレクト_GET() throws Exception {

		String expectedException = "取得対象のユーザが存在しません。ID：4";

		this.mockmvc.perform(get("/user/edit/4").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("exception", expectedException))
			.andExpect(redirectedUrl("/user/list/1"));
	}

	/************************************************
	 * 登録画面系テスト                             *
	 ************************************************/

	/**
	 * 未ログインでアクセス(GET)
	 *
	 * @throws Exception
	 */
	@Test
	@WithAnonymousUser
	void 未ログインで登録画面へ_GET() throws Exception {

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(get("/user/new"))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * 未ログインでアクセス(POST)
	 *
	 * @throws Exception
	 */
	@Test
	@WithAnonymousUser
	void 未ログインで登録画面へ_POST() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		req.setId(1);
		req.setLastName("テスト");
		req.setFirstName("ユーザ01");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("0");
		req.setBirthday("1997/10/20");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("1");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(post("/user/new")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * ログイン状態でアクセス（正常系・GET）
	 *
	 * @throws Exception
	 */
	@Test
	void ログイン済で登録画面へ_GET() throws Exception {

		UserEditRequestEntity blank = new UserEditRequestEntity();

		this.mockmvc.perform(get("/user/new").with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_REGISTER))
			.andExpect(model().attribute("userForEdit", blank))
			.andExpect(view().name("edit"));
	}

	/**
	 * ログイン状態でアクセス（正常系・POST）
	 *
	 * @throws Exception
	 */
	@Test
	void ログイン済で登録画面へ_POST() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		req.setId(1);
		req.setLastName("テスト");
		req.setFirstName("ユーザ01");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("0");
		req.setBirthday("1997/10/20");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("1");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		this.mockmvc.perform(post("/user/new")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().hasNoErrors())
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_REGISTER))
			.andExpect(model().attribute("userForEdit", req))
			.andExpect(view().name("edit"));
	}


	/************************************************
	 * 確認画面系テスト                             *
	 ************************************************/

	/**
	 * 未ログインでアクセス(POST)
	 *
	 * @throws Exception
	 */
	@Test
	@WithAnonymousUser
	void 未ログインで確認画面へ_POST() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		req.setId(1);
		req.setLastName("テスト");
		req.setFirstName("ユーザ01");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("0");
		req.setBirthday("1997/10/20");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("1");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(post("/user/confirm")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * ログイン済でアクセス(POST・登録時)
	 *
	 * @throws Exception
	 */
	@Test
	@WithAnonymousUser
	void ログイン済で確認画面へ_POST_登録時() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		// 登録時はIDはNULL
		req.setLastName("テスト");
		req.setFirstName("ユーザ01");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("0");
		req.setBirthday("1997/10/20");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("1");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		this.mockmvc.perform(post("/user/confirm")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_REGISTER))
			.andExpect(model().attribute("userForEdit", req))
			.andExpect(view().name("confirm"));
	}

	/**
	 * ログイン済でアクセス(POST・更新時)
	 *
	 * @throws Exception
	 */
	@Test
	@WithAnonymousUser
	void ログイン済で確認画面へ_POST_更新時() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();
		// 登録時はIDはNULL
		req.setId(1);
		req.setLastName("テスト");
		req.setFirstName("ユーザ01");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロイチ");
		req.setGender("0");
		req.setBirthday("1997/10/20");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("1");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		this.mockmvc.perform(post("/user/confirm")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_UPDATE))
			.andExpect(model().attribute("userForEdit", req))
			.andExpect(view().name("confirm"));
	}

	/**
	 * バリデーションチェック（異常系・必須チェック）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー_必須_確認画面() throws Exception {

		// すべての項目をnullでリクエスト
		UserConfirmRequestEntity req = new UserConfirmRequestEntity();

		MvcResult mvcResult = this.mockmvc.perform(post("/user/confirm")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userConfirmRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(10))
			.andExpect(status().isOk())
			.andExpect(view().name("edit"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userConfirmRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_lastNameKana = result.getFieldError("lastNameKana").getDefaultMessage();
		String actErr_firstNameKana = result.getFieldError("firstNameKana").getDefaultMessage();
		String actErr_lastName = result.getFieldError("lastName").getDefaultMessage();
		String actErr_firstName = result.getFieldError("firstName").getDefaultMessage();
		String actErr_gender = result.getFieldError("gender").getDefaultMessage();
		String actErr_birthday = result.getFieldError("birthday").getDefaultMessage();
		String actErr_postalCode = result.getFieldError("postalCode").getDefaultMessage();
		String actErr_prefecture = result.getFieldError("prefecture").getDefaultMessage();
		String actErr_address1 = result.getFieldError("address1").getDefaultMessage();
		String actErr_address2 = result.getFieldError("address2").getDefaultMessage();

		String expectErr_lastNameKana = "姓カナは必須項目です";
		String expectErr_firstNameKana = "名カナは必須項目です";
		String expectErr_lastName = "姓は必須項目です";
		String expectErr_firstName = "名は必須項目です";
		String expectErr_gender = "性別は必須項目です";
		String expectErr_birthday = "誕生日は必須項目です";
		String expectErr_postalCode = "郵便番号は必須項目です";
		String expectErr_prefecture = "都道府県は必須項目です";
		String expectErr_address1 = "市区町村は必須項目です";
		String expectErr_address2 = "町名は必須項目です";

		// メッセージ照合処理
		assertThat(expectErr_lastNameKana, is(actErr_lastNameKana));
		assertThat(expectErr_firstNameKana, is(actErr_firstNameKana));
		assertThat(expectErr_lastName, is(actErr_lastName));
		assertThat(expectErr_firstName, is(actErr_firstName));
		assertThat(expectErr_gender, is(actErr_gender));
		assertThat(expectErr_birthday, is(actErr_birthday));
		assertThat(expectErr_postalCode, is(actErr_postalCode));
		assertThat(expectErr_prefecture, is(actErr_prefecture));
		assertThat(expectErr_address1, is(actErr_address1));
		assertThat(expectErr_address2, is(actErr_address2));
	}

	/**
	 * バリデーションチェック（異常系・文字数チェック）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー_文字数_確認画面() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();

		// 必須チェックされる項目は正しく入力する
		req.setGender("0");
		req.setBirthday("2020/11/11");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");

		req.setLastName("aaaaaaaaaaa");											// 11文字（エラー）
		req.setFirstName("aaaaaaaaaaa");										// 11文字（エラー）
		req.setLastNameKana("アアアアアアアアアアアアアアアアアアアアア");		// 21文字（エラー）
		req.setFirstNameKana("アアアアアアアアアアアアアアアアアアアアア");		// 21文字（エラー）
		req.setAddress1("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setAddress2("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setAddress3("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setAddress4("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setPhoneNumber("000000000000");										// 12文字（エラー）
		req.setRemarks("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");	// 401文字（エラー）
		req.setInsertUser("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");	// 46文字（エラー）
		req.setUpdateUser("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");	// 46文字（エラー）

		MvcResult mvcResult = this.mockmvc.perform(post("/user/confirm")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userConfirmRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(12))
			.andExpect(status().isOk())
			.andExpect(view().name("edit"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userConfirmRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_lastName = result.getFieldError("lastName").getDefaultMessage();
		String actErr_firstName = result.getFieldError("firstName").getDefaultMessage();
		String actErr_lastNameKana = result.getFieldError("lastNameKana").getDefaultMessage();
		String actErr_firstNameKana = result.getFieldError("firstNameKana").getDefaultMessage();
		String actErr_address1 = result.getFieldError("address1").getDefaultMessage();
		String actErr_address2 = result.getFieldError("address2").getDefaultMessage();
		String actErr_address3 = result.getFieldError("address3").getDefaultMessage();
		String actErr_address4 = result.getFieldError("address4").getDefaultMessage();
		String actErr_phoneNumber = result.getFieldError("phoneNumber").getDefaultMessage();
		String actErr_remarks = result.getFieldError("remarks").getDefaultMessage();
		String actErr_insertUser = result.getFieldError("insertUser").getDefaultMessage();
		String actErr_updateUser = result.getFieldError("updateUser").getDefaultMessage();

		String expectErr_lastName = "姓は10文字以内で入力してください";
		String expectErr_firstName = "名は10文字以内で入力してください";
		String expectErr_lastNameKana = "姓カナは20文字以内で入力してください";
		String expectErr_firstNameKana = "名カナは20文字以内で入力してください";
		String expectErr_address1 = "市区町村は30文字以内で入力してください";
		String expectErr_address2 = "町名は30文字以内で入力してください";
		String expectErr_address3 = "番地は30文字以内で入力してください";
		String expectErr_address4 = "建物名・部屋番号は30文字以内で入力してください";
		String expectErr_phoneNumber = "電話番号は11文字以内で入力してください";
		String expectErr_remarks = "備考は400文字以内で入力してください";
		String expectErr_insertUser = "登録者は45文字以内で入力してください";
		String expectErr_updateUser = "更新者は45文字以内で入力してください";

		// メッセージ照合処理
		assertThat(expectErr_lastNameKana, is(actErr_lastNameKana));
		assertThat(expectErr_firstNameKana, is(actErr_firstNameKana));
		assertThat(expectErr_lastName, is(actErr_lastName));
		assertThat(expectErr_firstName, is(actErr_firstName));
		assertThat(expectErr_address1, is(actErr_address1));
		assertThat(expectErr_address2, is(actErr_address2));
		assertThat(expectErr_address3, is(actErr_address3));
		assertThat(expectErr_address4, is(actErr_address4));
		assertThat(expectErr_phoneNumber, is(actErr_phoneNumber));
		assertThat(expectErr_remarks, is(actErr_remarks));
		assertThat(expectErr_insertUser, is(actErr_insertUser));
		assertThat(expectErr_updateUser, is(actErr_updateUser));
	}

	/**
	 * バリデーションチェック（異常系・その他チェック）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー_その他_確認画面() throws Exception {

		UserConfirmRequestEntity req = new UserConfirmRequestEntity();

		// チェック外の項目は正しく入力する
		req.setLastName("テスト");
		req.setFirstName("ユーザ");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		req.setLastNameKana("てすと");		// ひらがな（エラー）
		req.setFirstNameKana("登録");		// 漢字（エラー）
		req.setGender("2");					// 0: 男性・1: 女性（エラー）
		req.setBirthday("2020/33/33");		// 日付以外（エラー）
		req.setPostalCode("123456");		// 7桁でない（エラー）
		req.setPhoneNumber("aaaaaaaaaa");	// 数値でない（エラー）

		MvcResult mvcResult = this.mockmvc.perform(post("/user/confirm")
				.flashAttr("userConfirmRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userConfirmRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(6))
			.andExpect(status().isOk())
			.andExpect(view().name("edit"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userConfirmRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_lastNameKana = result.getFieldError("lastNameKana").getDefaultMessage();
		String actErr_firstNameKana = result.getFieldError("firstNameKana").getDefaultMessage();
		String actErr_gender = result.getFieldError("gender").getDefaultMessage();
		String actErr_birthday = result.getFieldError("birthday").getDefaultMessage();
		String actErr_postalCode = result.getFieldError("postalCode").getDefaultMessage();
		String actErr_phoneNumber = result.getFieldError("phoneNumber").getDefaultMessage();

		String expectErr_lastNameKana = "姓カナは全角カタカナで入力してください";
		String expectErr_firstNameKana = "名カナは全角カタカナで入力してください";
		String expectErr_gender = "性別の入力値に誤りがあります";
		String expectErr_birthday = "誕生日の入力値に誤りがあります";
		String expectErr_postalCode = "郵便番号の入力値に誤りがあります";
		String expectErr_phoneNumber = "電話番号の入力値に誤りがあります";

		// メッセージ照合処理
		assertThat(expectErr_lastNameKana, is(actErr_lastNameKana));
		assertThat(expectErr_firstNameKana, is(actErr_firstNameKana));
		assertThat(expectErr_gender, is(actErr_gender));
		assertThat(expectErr_birthday, is(actErr_birthday));
		assertThat(expectErr_postalCode, is(actErr_postalCode));
		assertThat(expectErr_phoneNumber, is(actErr_phoneNumber));
	}

	/************************************************
	 * 登録処理テスト                               *
	 ************************************************/

	/**
	 * 未ログインでアクセス(POST)
	 *
	 * @throws Exception
	 */
	@Test
	@WithAnonymousUser
	void 未ログインで登録処理() throws Exception {

		// 未ログインでのアクセスの場合、ログイン画面へリダイレクトされる
		this.mockmvc.perform(post("/user/insert")
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * ログイン済でアクセス(POST)
	 *
	 * @throws Exception
	 */
	@Test
	@DatabaseSetup("/data/")
	@ExpectedDatabase(value = "/expect/insert/", assertionMode=DatabaseAssertionMode.NON_STRICT)
	@Transactional
	void ログイン済で登録処理() throws Exception {

		UserEditRequestEntity req = new UserEditRequestEntity();
		req.setLastName("テスト");
		req.setFirstName("ユーザ04");
		req.setLastNameKana("テスト");
		req.setFirstNameKana("ユーザゼロヨン");
		req.setGender("0");
		req.setBirthday("1944/04/04");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setAddress3("4");
		req.setPhoneNumber("0000000000");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		this.mockmvc.perform(post("/user/insert")
				.flashAttr("userEditRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("information", UserConstant.REGISTER_SUCCESS))
			.andExpect(redirectedUrl("/user/list/1"));
	}

	/**
	 * バリデーションチェック（異常系・必須チェック）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー_必須_登録処理() throws Exception {

		// すべての項目をnullでリクエスト
		UserEditRequestEntity req = new UserEditRequestEntity();

		MvcResult mvcResult = this.mockmvc.perform(post("/user/insert")
				.flashAttr("userEditRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userEditRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(10))
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_REGISTER))
			.andExpect(view().name("edit"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userEditRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_lastNameKana = result.getFieldError("lastNameKana").getDefaultMessage();
		String actErr_firstNameKana = result.getFieldError("firstNameKana").getDefaultMessage();
		String actErr_lastName = result.getFieldError("lastName").getDefaultMessage();
		String actErr_firstName = result.getFieldError("firstName").getDefaultMessage();
		String actErr_gender = result.getFieldError("gender").getDefaultMessage();
		String actErr_birthday = result.getFieldError("birthday").getDefaultMessage();
		String actErr_postalCode = result.getFieldError("postalCode").getDefaultMessage();
		String actErr_prefecture = result.getFieldError("prefecture").getDefaultMessage();
		String actErr_address1 = result.getFieldError("address1").getDefaultMessage();
		String actErr_address2 = result.getFieldError("address2").getDefaultMessage();

		String expectErr_lastNameKana = "姓カナは必須項目です";
		String expectErr_firstNameKana = "名カナは必須項目です";
		String expectErr_lastName = "姓は必須項目です";
		String expectErr_firstName = "名は必須項目です";
		String expectErr_gender = "性別は必須項目です";
		String expectErr_birthday = "誕生日は必須項目です";
		String expectErr_postalCode = "郵便番号は必須項目です";
		String expectErr_prefecture = "都道府県は必須項目です";
		String expectErr_address1 = "市区町村は必須項目です";
		String expectErr_address2 = "町名は必須項目です";

		// メッセージ照合処理
		assertThat(expectErr_lastNameKana, is(actErr_lastNameKana));
		assertThat(expectErr_firstNameKana, is(actErr_firstNameKana));
		assertThat(expectErr_lastName, is(actErr_lastName));
		assertThat(expectErr_firstName, is(actErr_firstName));
		assertThat(expectErr_gender, is(actErr_gender));
		assertThat(expectErr_birthday, is(actErr_birthday));
		assertThat(expectErr_postalCode, is(actErr_postalCode));
		assertThat(expectErr_prefecture, is(actErr_prefecture));
		assertThat(expectErr_address1, is(actErr_address1));
		assertThat(expectErr_address2, is(actErr_address2));
	}

	/**
	 * バリデーションチェック（異常系・文字数チェック）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー_文字数_登録処理() throws Exception {

		UserEditRequestEntity req = new UserEditRequestEntity();

		// 必須チェックされる項目は正しく入力する
		req.setGender("0");
		req.setBirthday("2020/11/11");
		req.setPostalCode("1234567");
		req.setPrefecture("東京都");

		req.setLastName("aaaaaaaaaaa");											// 11文字（エラー）
		req.setFirstName("aaaaaaaaaaa");										// 11文字（エラー）
		req.setLastNameKana("アアアアアアアアアアアアアアアアアアアアア");		// 21文字（エラー）
		req.setFirstNameKana("アアアアアアアアアアアアアアアアアアアアア");		// 21文字（エラー）
		req.setAddress1("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setAddress2("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setAddress3("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setAddress4("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");						// 31文字（エラー）
		req.setPhoneNumber("000000000000");										// 12文字（エラー）
		req.setRemarks("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");	// 401文字（エラー）
		req.setInsertUser("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");	// 46文字（エラー）
		req.setUpdateUser("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");	// 46文字（エラー）

		MvcResult mvcResult = this.mockmvc.perform(post("/user/insert")
				.flashAttr("userEditRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userEditRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(12))
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_REGISTER))
			.andExpect(view().name("edit"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userEditRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_lastName = result.getFieldError("lastName").getDefaultMessage();
		String actErr_firstName = result.getFieldError("firstName").getDefaultMessage();
		String actErr_lastNameKana = result.getFieldError("lastNameKana").getDefaultMessage();
		String actErr_firstNameKana = result.getFieldError("firstNameKana").getDefaultMessage();
		String actErr_address1 = result.getFieldError("address1").getDefaultMessage();
		String actErr_address2 = result.getFieldError("address2").getDefaultMessage();
		String actErr_address3 = result.getFieldError("address3").getDefaultMessage();
		String actErr_address4 = result.getFieldError("address4").getDefaultMessage();
		String actErr_phoneNumber = result.getFieldError("phoneNumber").getDefaultMessage();
		String actErr_remarks = result.getFieldError("remarks").getDefaultMessage();
		String actErr_insertUser = result.getFieldError("insertUser").getDefaultMessage();
		String actErr_updateUser = result.getFieldError("updateUser").getDefaultMessage();

		String expectErr_lastName = "姓は10文字以内で入力してください";
		String expectErr_firstName = "名は10文字以内で入力してください";
		String expectErr_lastNameKana = "姓カナは20文字以内で入力してください";
		String expectErr_firstNameKana = "名カナは20文字以内で入力してください";
		String expectErr_address1 = "市区町村は30文字以内で入力してください";
		String expectErr_address2 = "町名は30文字以内で入力してください";
		String expectErr_address3 = "番地は30文字以内で入力してください";
		String expectErr_address4 = "建物名・部屋番号は30文字以内で入力してください";
		String expectErr_phoneNumber = "電話番号は11文字以内で入力してください";
		String expectErr_remarks = "備考は400文字以内で入力してください";
		String expectErr_insertUser = "登録者は45文字以内で入力してください";
		String expectErr_updateUser = "更新者は45文字以内で入力してください";

		// メッセージ照合処理
		assertThat(expectErr_lastNameKana, is(actErr_lastNameKana));
		assertThat(expectErr_firstNameKana, is(actErr_firstNameKana));
		assertThat(expectErr_lastName, is(actErr_lastName));
		assertThat(expectErr_firstName, is(actErr_firstName));
		assertThat(expectErr_address1, is(actErr_address1));
		assertThat(expectErr_address2, is(actErr_address2));
		assertThat(expectErr_address3, is(actErr_address3));
		assertThat(expectErr_address4, is(actErr_address4));
		assertThat(expectErr_phoneNumber, is(actErr_phoneNumber));
		assertThat(expectErr_remarks, is(actErr_remarks));
		assertThat(expectErr_insertUser, is(actErr_insertUser));
		assertThat(expectErr_updateUser, is(actErr_updateUser));
	}

	/**
	 * バリデーションチェック（異常系・その他チェック）
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	void バリデーションエラー_その他_登録処理() throws Exception {

		UserEditRequestEntity req = new UserEditRequestEntity();

		// チェック外の項目は正しく入力する
		req.setLastName("テスト");
		req.setFirstName("ユーザ");
		req.setPrefecture("東京都");
		req.setAddress1("A市");
		req.setAddress2("B町");
		req.setRemarks("test");
		req.setInsertUser("system");
		req.setUpdateUser("system");

		req.setLastNameKana("てすと");		// ひらがな（エラー）
		req.setFirstNameKana("登録");		// 漢字（エラー）
		req.setGender("2");					// 0: 男性・1: 女性（エラー）
		req.setBirthday("2020/33/33");		// 日付以外（エラー）
		req.setPostalCode("123456");		// 7桁でない（エラー）
		req.setPhoneNumber("aaaaaaaaaa");	// 数値でない（エラー）

		MvcResult mvcResult = this.mockmvc.perform(post("/user/insert")
				.flashAttr("userEditRequestEntity", req)
				// POSTの場合、以下によりCSRFトークンを発行しないと403(FORBIDDEN)となる
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(user("username").roles("USER")))
			.andDo(print())
			.andExpect(model().attributeHasErrors("userEditRequestEntity"))
			// エラー件数の確認
			.andExpect(model().errorCount(6))
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", UserConstant.TITLE_REGISTER))
			.andExpect(view().name("edit"))
			// ↓を付けるとMvcResultのオブジェクトを取得できる
			.andReturn();

		// バリデーションエラーメッセージの取得
		ModelAndView mav = mvcResult.getModelAndView();

		BindingResult result = (BindingResult) mav.getModel()
				.get("org.springframework.validation.BindingResult.userEditRequestEntity");

		// バリデーションエラーメッセージの確認
		String actErr_lastNameKana = result.getFieldError("lastNameKana").getDefaultMessage();
		String actErr_firstNameKana = result.getFieldError("firstNameKana").getDefaultMessage();
		String actErr_gender = result.getFieldError("gender").getDefaultMessage();
		String actErr_birthday = result.getFieldError("birthday").getDefaultMessage();
		String actErr_postalCode = result.getFieldError("postalCode").getDefaultMessage();
		String actErr_phoneNumber = result.getFieldError("phoneNumber").getDefaultMessage();

		String expectErr_lastNameKana = "姓カナは全角カタカナで入力してください";
		String expectErr_firstNameKana = "名カナは全角カタカナで入力してください";
		String expectErr_gender = "性別の入力値に誤りがあります";
		String expectErr_birthday = "誕生日の入力値に誤りがあります";
		String expectErr_postalCode = "郵便番号の入力値に誤りがあります";
		String expectErr_phoneNumber = "電話番号の入力値に誤りがあります";

		// メッセージ照合処理
		assertThat(expectErr_lastNameKana, is(actErr_lastNameKana));
		assertThat(expectErr_firstNameKana, is(actErr_firstNameKana));
		assertThat(expectErr_gender, is(actErr_gender));
		assertThat(expectErr_birthday, is(actErr_birthday));
		assertThat(expectErr_postalCode, is(actErr_postalCode));
		assertThat(expectErr_phoneNumber, is(actErr_phoneNumber));
	}

	/************************************************
	 * 更新処理テスト                               *
	 ************************************************/






	/************************************************
	 * 削除処理テスト                               *
	 ************************************************/







}
