package com.paymybuddy.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@ExtendWith(MockitoExtension.class)
public class TransactionTransactionalTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@MockBean
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
	void executeTransaction_whenExceptionIsThrown_doNotSaveTransaction() throws Exception {
		when(transactionRepository.save(any(Transaction.class))).thenThrow(new MockitoException("transactional test"));

		assertThatThrownBy(() -> { // Because controller does not know how to handle mockitoException we just
									// verify we are the ones who thrown the exception
			mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
					.param("connection", "4").param("amount", "3.50"));
		}).hasCauseInstanceOf(MockitoException.class);

		Wallet sender = walletRepository.findById(3L).orElseThrow();
		Wallet receiver = walletRepository.findById(2L).orElseThrow();

		// Because there was an Exception the wallet amounts should not have changed.
		assertThat(sender.getAmount()).isEqualTo(new BigDecimal(1000).setScale(2));
		assertThat(receiver.getAmount()).isEqualTo(new BigDecimal(1000).setScale(2));
	}
}
