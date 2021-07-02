package com.user.mng.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class UserControllerTest {

	private MockMvc mockmvc;

	@Autowired
	UserController target;

	@Before
	public void setup() {
		mockmvc = MockMvcBuilders.standaloneSetup(target).build();
	}

	@Test
	void isStatusTest() throws Exception {
		mockmvc.perform(get("/user/list/1")).andExpect(status().isOk());
	}

}
