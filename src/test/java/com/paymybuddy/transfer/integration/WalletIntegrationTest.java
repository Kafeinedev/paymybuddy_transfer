package com.paymybuddy.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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

import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.repository.WalletRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "pas@pauvre.com", password = "Simp@PortM0nnaie")
public class WalletIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private WalletRepository walletRepository;

	@BeforeEach
	private void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@AfterEach
	void cleanUp() {
		List<Wallet> toMopUp = walletRepository.findAll();

		for (Wallet wallet : toMopUp) {
			if (wallet.getId() != 1 && wallet.getId() != 2 && wallet.getId() != 3) {
				walletRepository.delete(wallet);
			}
		}
	}

	@Test
	void create_wallet() throws Exception {
		mockMvc.perform(post("/wallet").with(SecurityMockMvcRequestPostProcessors.csrf()).param("currency", "EUR"))
				.andExpect(status().is2xxSuccessful());

		List<Wallet> all = walletRepository.findAll(Sort.by("id"));
		Wallet test = all.get(all.size() - 1);

		assertThat(test.getCurrency()).isEqualTo("EUR");
		assertThat(test.getAmount()).isEqualTo(BigDecimal.ZERO.setScale(2));
		assertThat(test.getOwner().getId()).isEqualTo(3L);
	}
}
