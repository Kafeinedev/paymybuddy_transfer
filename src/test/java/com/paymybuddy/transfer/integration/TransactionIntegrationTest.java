package com.paymybuddy.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
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

import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.repository.ITransactionRepository;
import com.paymybuddy.transfer.repository.IWalletRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "pas@pauvre.com", password = "Simp@PortM0nnaie")
public class TransactionIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ITransactionRepository transactionRepository;

	@Autowired
	private IWalletRepository walletRepository;

	@BeforeEach
	private void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@BeforeAll
	void fundWallets() {
		Wallet sender = walletRepository.findById(3L).orElseThrow();
		Wallet receiver = walletRepository.findById(2L).orElseThrow();
		sender.setAmount(new BigDecimal(1000));
		receiver.setAmount(new BigDecimal(1000));
		walletRepository.save(sender);
		walletRepository.save(receiver);
	}

	@Test
	void execute_transaction() throws Exception {
		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "4")
				.param("amount", "3.50")).andExpect(status().is3xxRedirection());

		List<Transaction> testList = transactionRepository.findAll(Sort.by("id"));
		Transaction test = testList.get(testList.size() - 1);
		Wallet sender = walletRepository.findById(3L).orElseThrow();
		Wallet receiver = walletRepository.findById(2L).orElseThrow();

		assertThat(test.getAmount()).isEqualTo(new BigDecimal(3.50).setScale(2, RoundingMode.HALF_UP));
		assertThat(test.getFee()).isEqualTo(new BigDecimal(0.02).setScale(2, RoundingMode.HALF_UP));
		assertThat(test.getDate()).isNotNull();
		assertThat(test.getDescription()).isEqualTo("");
		assertThat(sender.getAmount()).isEqualTo(new BigDecimal(996.48).setScale(2, RoundingMode.HALF_UP));
		assertThat(receiver.getAmount()).isEqualTo(new BigDecimal(1003.50).setScale(2, RoundingMode.HALF_UP));
	}

	@Test
	void updating_transactionDescription() throws Exception {
		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "4")
				.param("amount", "0")).andExpect(status().is3xxRedirection());

		List<Transaction> testList = transactionRepository.findAll(Sort.by("id"));
		Transaction test = testList.get(testList.size() - 1);

		mockMvc.perform(patch("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("transactionId", Long.toString(test.getId())).param("newDescription", "new description"))
				.andExpect(status().is2xxSuccessful());

		test = transactionRepository.findById(test.getId()).orElseThrow();
		assertThat(test.getDescription()).isEqualTo("new description");
	}

	@Test
	void displaying_mytransactions() throws Exception {
		mockMvc.perform(get("/mytransactions")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("transactions")).andExpect(model().size(5))
				.andExpect(model().attributeExists("connections")).andExpect(model().attributeExists("currentPage"))
				.andExpect(model().attributeExists("totalPages")).andExpect(model().attributeExists("totalItems"))
				.andExpect(model().attributeExists("transactions"));
	}
}
