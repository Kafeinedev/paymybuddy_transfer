package com.paymybuddy.transfer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.transfer.controller.WalletController;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.service.IWalletService;

@WebMvcTest(WalletController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "a@dress.com")
class WalletControllerTest {

	@MockBean
	private IWalletService mockWalletService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createWallet_whenCalled_return2xxAndWallet() throws Exception {
		Wallet wallet = new Wallet();
		when(mockWalletService.createWallet("a@dress.com", "EUR")).thenReturn(wallet);

		mockMvc.perform(post("/wallet").with(SecurityMockMvcRequestPostProcessors.csrf()).param("currency", "EUR"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string("{\"id\":0,\"currency\":null,\"amount\":0.00}"));
	}

	@Test
	void createWallet_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/wallet").param("currency", "EUR")).andExpect(status().isForbidden());
	}

	@Test
	void createWallet_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/wallet").with(SecurityMockMvcRequestPostProcessors.csrf()).param("currency", "EUR"));

		verify(mockWalletService, times(1)).createWallet("a@dress.com", "EUR");
	}

	@Test
	void createWallet_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockWalletService.createWallet("a@dress.com", "EUR")).thenThrow(new InvalidArgumentException());

		mockMvc.perform(post("/wallet").with(SecurityMockMvcRequestPostProcessors.csrf()).param("currency", "EUR"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createWallet_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockWalletService.createWallet("a@dress.com", "EUR")).thenThrow(new EntityMissingException());

		mockMvc.perform(post("/wallet").with(SecurityMockMvcRequestPostProcessors.csrf()).param("currency", "EUR"))
				.andExpect(status().isNotFound());
	}

}
