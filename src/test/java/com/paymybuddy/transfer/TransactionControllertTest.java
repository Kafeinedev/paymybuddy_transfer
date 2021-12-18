package com.paymybuddy.transfer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.transfer.controller.TransactionController;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.service.TransactionService;
import com.paymybuddy.transfer.service.UserService;
import com.paymybuddy.transfer.service.WalletLinkService;

@WebMvcTest(TransactionController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "a@dress.com")
class TransactionControllertTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService mockUserService;

	@MockBean
	private WalletLinkService mockWalletLinkService;

	@MockBean
	private TransactionService mockTransactionService;

	@Test
	void getMyTransactions_whenCalled_return2xxWithModelAndView() throws Exception {
		Page<String[]> page = new PageImpl<String[]>(new ArrayList<String[]>());
		List<WalletLink> links = new ArrayList<WalletLink>();

		when(mockTransactionService.getTransactionsInfoByUserEmailAndPage("a@dress.com", 0)).thenReturn(page);
		when(mockWalletLinkService.getAllOutgoingLinksByUserEmail("a@dress.com")).thenReturn(links);

		mockMvc.perform(get("/mytransactions")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("transactions")).andExpect(model().size(5))
				.andExpect(model().attributeExists("connections")).andExpect(model().attributeExists("currentPage"))
				.andExpect(model().attributeExists("totalPages")).andExpect(model().attributeExists("totalItems"))
				.andExpect(model().attributeExists("transactions"));
	}

	@Test
	void getMyTransactions_whenCalled_callService() throws Exception {
		Page<String[]> page = new PageImpl<String[]>(new ArrayList<String[]>());
		List<WalletLink> links = new ArrayList<WalletLink>();

		when(mockTransactionService.getTransactionsInfoByUserEmailAndPage("a@dress.com", 0)).thenReturn(page);
		when(mockWalletLinkService.getAllOutgoingLinksByUserEmail("a@dress.com")).thenReturn(links);

		mockMvc.perform(get("/mytransactions"));

		verify(mockTransactionService, times(1)).getTransactionsInfoByUserEmailAndPage(any(String.class),
				any(Integer.class));
		verify(mockWalletLinkService, times(1)).getAllOutgoingLinksByUserEmail(any(String.class));
	}

	@Test
	void getMyTransactions_whenServiceThrowInvalidArgumentException_return400BadRequest() throws Exception {
		List<WalletLink> links = new ArrayList<WalletLink>();
		when(mockWalletLinkService.getAllOutgoingLinksByUserEmail("a@dress.com")).thenReturn(links);
		doThrow(new InvalidArgumentException()).when(mockTransactionService)
				.getTransactionsInfoByUserEmailAndPage(any(String.class), any(Integer.class));

		mockMvc.perform(get("/mytransactions?page=-1")).andExpect(status().isBadRequest());
	}

	@Test
	@WithAnonymousUser
	void getMyTransactions_whenCalledByAnonymousUser_isRedirectedToLogin() throws Exception {
		mockMvc.perform(get("/mytransactions")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));// spring security want to redirect toward this not
																	// just /login
	}

	@Test
	void executeTransaction_whenCalled_return3xxAndRedirectTowardMyTransactions() throws Exception {
		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/mytransactions"));
	}

	@Test
	void executeTransaction_whenCalled_callService() throws Exception {
		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00"));

		verify(mockTransactionService, times(1)).makeTransaction("a@dress.com", 1L, new BigDecimal("10.00"), null);
	}

	@Test
	void executeTransaction_whenCalledWithoutCsrfToken_return4xx() throws Exception {
		mockMvc.perform(post("/transaction").param("connection", "1").param("amount", "10.00"))
				.andExpect(status().isForbidden());
	}

	@Test
	void executeTransaction_whenCalledWithoutCsrfToken_doNotExecuteTransaction() throws Exception {
		mockMvc.perform(post("/transaction").param("connection", "1").param("amount", "10.00"));

		verify(mockTransactionService, times(0)).makeTransaction(any(String.class), any(Long.class),
				any(BigDecimal.class), any());
	}

	@Test
	@WithAnonymousUser
	void executeTransaction_whenCalledByAnonymousUser_isRedirectedToLogin() throws Exception {
		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));
	}

	@Test
	@WithAnonymousUser
	void executeTransaction_whenCalledByAnonymousUser_doNotExecuteTransaction() throws Exception {
		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00"));

		verify(mockTransactionService, times(0)).makeTransaction(any(String.class), any(Long.class),
				any(BigDecimal.class), any());
	}

	@Test
	void executeTransaction_whenServiceThrowEntityMissingException_return404NotFound() throws Exception {
		doThrow(new EntityMissingException()).when(mockTransactionService).makeTransaction(any(String.class),
				any(Long.class), any(BigDecimal.class), any());

		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00")).andExpect(status().isNotFound());
	}

	@Test
	void executeTransaction_whenServicethrowInsufficientFundException_return400BadRequest() throws Exception {
		doThrow(new InsufficientFundException()).when(mockTransactionService).makeTransaction(any(String.class),
				any(Long.class), any(BigDecimal.class), any());

		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00")).andExpect(status().isBadRequest());
	}

	@Test
	void executeTransaction_whenServicethrowWrongUserException_return401unauthorized() throws Exception {
		doThrow(new WrongUserException()).when(mockTransactionService).makeTransaction(any(String.class),
				any(Long.class), any(BigDecimal.class), any());

		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00")).andExpect(status().isUnauthorized());
	}

	@Test
	void executeTransaction_whenServiceInvalidArgumentException_return400BadRequest() throws Exception {
		doThrow(new InvalidArgumentException()).when(mockTransactionService).makeTransaction(any(String.class),
				any(Long.class), any(BigDecimal.class), any());

		mockMvc.perform(post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf()).param("connection", "1")
				.param("amount", "10.00")).andExpect(status().isBadRequest());
	}
}
