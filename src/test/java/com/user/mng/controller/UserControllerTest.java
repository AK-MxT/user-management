package com.user.mng.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.user.mng.domain.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
	void getListTest() throws Exception {
		mockmvc.perform(get("/user/list/1")).andExpect(status().is3xxRedirection());
		//		.andExpect(view().name("login"));
	}
}
