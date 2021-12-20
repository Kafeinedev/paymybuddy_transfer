package com.paymybuddy.transfer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.transfer.controller.LoginController;

@WebMvcTest(controllers = LoginController.class)
class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void login_whenGetRequest_return2xxwithLoginPage() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().is2xxSuccessful()).andExpect(view().name("login"));
	}

	@Test
	@WithMockUser()
	void login_ifUserAlreadyLoggedIn_return3xxWithRedirectView() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/mytransactions"));
	}

}
