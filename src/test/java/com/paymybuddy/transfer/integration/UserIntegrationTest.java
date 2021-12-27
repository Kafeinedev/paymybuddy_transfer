package com.paymybuddy.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.repository.UserRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	private void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@AfterEach
	void cleanUp() {
		List<User> toMopUp = userRepository.findAll();

		for (User user : toMopUp) {
			if (user.getId() != 1 && user.getId() != 2 && user.getId() != 3) {
				userRepository.delete(user);
			}
		}
	}

	@Test
	@WithAnonymousUser
	void creating_User() throws Exception {
		mockMvc.perform(post("/createuser").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "a@a.a").param("password", "P@ssword1")).andExpect(status().is2xxSuccessful());

		List<User> testList = userRepository.findAll(Sort.by("id"));
		User test = testList.get(testList.size() - 1);

		assertThat(test.getName()).isEqualTo("name");
		assertThat(test.getEmail()).isEqualTo("a@a.a");
		assertThat(test.getPassword()).isNotEqualTo("P@ssword1");
	}

	@Test
	@WithMockUser(username = "a@a.a", password = "P@ssword1")
	void updating_User() throws Exception {
		mockMvc.perform(post("/createuser").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("email", "a@a.a").param("password", "P@ssword1")).andExpect(status().is2xxSuccessful());

		List<User> testList = userRepository.findAll(Sort.by("id"));
		User test = testList.get(testList.size() - 1);
		String formerPassword = test.getPassword();

		mockMvc.perform(put("/user").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "no name")
				.param("email", "b@b.b").param("password", "P@ssword2")).andExpect(status().is2xxSuccessful());

		test = userRepository.findById(test.getId()).orElse(new User());

		assertThat(test.getName()).isEqualTo("no name");
		assertThat(test.getEmail()).isEqualTo("b@b.b");
		assertThat(test.getPassword()).isNotEqualTo(formerPassword);
	}
}
