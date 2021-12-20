package com.paymybuddy.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.IWalletLinkRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "pas@pauvre.com", password = "Simp@PortM0nnaie")
public class WalletLinkIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IWalletLinkRepository walletLinkRepository;

	@BeforeEach
	private void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@AfterEach
	void cleanUp() {
		List<WalletLink> toMopUp = walletLinkRepository.findAll();

		for (WalletLink link : toMopUp) {
			if (link.getId() != 1 && link.getId() != 2 && link.getId() != 3 && link.getId() != 4) {
				walletLinkRepository.delete(link);
			}
		}
	}

	@Test
	void creating_walletLink() throws Exception {
		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "3").param("receiverId", "1")).andExpect(status().is2xxSuccessful());

		List<WalletLink> testList = walletLinkRepository.findAll(Sort.by("id"));
		WalletLink test = testList.get(testList.size() - 1);

		assertThat(test.getName()).isEqualTo("name");
	}

	@Test
	void updating_walletLinkName() throws Exception {
		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "3").param("receiverId", "1")).andExpect(status().is2xxSuccessful());

		List<WalletLink> testList = walletLinkRepository.findAll(Sort.by("id"));
		WalletLink test = testList.get(testList.size() - 1);

		mockMvc.perform(patch("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("walletLinkId", Long.toString(test.getId())).param("newName", "new name"))
				.andExpect(status().is2xxSuccessful());

		test = walletLinkRepository.findById(test.getId()).orElse(new WalletLink());

		assertThat(test.getName()).isEqualTo("new name");
	}
}
