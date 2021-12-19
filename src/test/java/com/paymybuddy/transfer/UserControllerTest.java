package com.paymybuddy.transfer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.transfer.controller.UserController;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.service.UserService;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "a@dress.com")
class UserControllerTest {

	@MockBean
	private UserService mockUserService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithAnonymousUser
	void createUser_whenCalled_return2xxAndCreatedUser() throws Exception {
		User user = new User();
		when(mockUserService.createUser("name", "email", "pass")).thenReturn(user);

		mockMvc.perform(post("/createuser").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass")).andExpect(status().is2xxSuccessful())
				.andExpect(content().string(
						"{\"id\":0,\"email\":null,\"name\":null,\"password\":null,\"wallets\":null,\"bankCoordinates\":null}"));
	}

	@Test
	@WithAnonymousUser
	void createUser_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/createuser").param("name", "name").param("email", "email").param("password", "pass"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	void createUser_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/createuser").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass"));

		verify(mockUserService, times(1)).createUser("name", "email", "pass");
	}

	@Test
	@WithAnonymousUser
	void createUser_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockUserService.createUser("name", "email", "pass")).thenThrow(new InvalidArgumentException());

		mockMvc.perform(post("/createuser").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass")).andExpect(status().isBadRequest());
	}

	@Test
	void updateUser_whenCalled_return2xxAndUpdatedUser() throws Exception {
		User user = new User();
		when(mockUserService.updateUser("a@dress.com", "name", "email", "pass")).thenReturn(user);

		mockMvc.perform(put("/user").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass")).andExpect(status().is2xxSuccessful())
				.andExpect(content().string(
						"{\"id\":0,\"email\":null,\"name\":null,\"password\":null,\"wallets\":null,\"bankCoordinates\":null}"));
	}

	@Test
	void updateUser_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(put("/user").param("name", "name").param("email", "email").param("password", "pass"))
				.andExpect(status().isForbidden());
	}

	@Test
	void updateUser_whenCalled_useService() throws Exception {
		mockMvc.perform(put("/user").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass"));

		verify(mockUserService, times(1)).updateUser("a@dress.com", "name", "email", "pass");
	}

	@Test
	void updateUser_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockUserService.updateUser("a@dress.com", "name", "email", "pass"))
				.thenThrow(new InvalidArgumentException());

		mockMvc.perform(put("/user").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass")).andExpect(status().isBadRequest());
	}

	@Test
	void updateUser_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockUserService.updateUser("a@dress.com", "name", "email", "pass"))
				.thenThrow(new EntityMissingException());

		mockMvc.perform(put("/user").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "email").param("password", "pass")).andExpect(status().isNotFound());
	}
}
