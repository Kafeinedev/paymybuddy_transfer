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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.paymybuddy.transfer.controller.TransactionController;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.service.TransactionService;
import com.paymybuddy.transfer.service.UserService;

@WebMvcTest(controllers = TransactionController.class)
@ExtendWith(MockitoExtension.class)
class TransactionControllertTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@MockBean
	private UserService mockUserService;

	@MockBean
	private TransactionService mockTransactionService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void getMyTransactions_whenCalled_return2xxWithModelAndView() throws Exception {
		Page<String[]> page = new PageImpl<String[]>(new ArrayList<String[]>());
		List<WalletLink> links = new ArrayList<WalletLink>();

		when(mockUserService.getTransactionsInfoByUserEmailAndPage(any(String.class), any(Integer.class)))
				.thenReturn(page);
		when(mockUserService.getAllOutgoingLinksByUserEmail(any(String.class))).thenReturn(links);

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

		when(mockUserService.getTransactionsInfoByUserEmailAndPage(any(String.class), any(Integer.class)))
				.thenReturn(page);
		when(mockUserService.getAllOutgoingLinksByUserEmail(any(String.class))).thenReturn(links);

		mockMvc.perform(get("/mytransactions"));

		verify(mockUserService, times(1)).getTransactionsInfoByUserEmailAndPage(any(String.class), any(Integer.class));
		verify(mockUserService, times(1)).getAllOutgoingLinksByUserEmail(any(String.class));
	}

	@Test
	void executeTransaction_whenCalled_return3xxAndRedirectTowardMyTransactions() throws Exception {
		mockMvc.perform(post("/transaction").param("connection", "1").param("amount", "10.00"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/mytransactions"));
	}

	@Test
	void executeTransaction_whenCalled_callService() throws Exception {
		mockMvc.perform(post("/transaction").param("connection", "1").param("amount", "10.00"));

		verify(mockTransactionService, times(1)).makeTransaction(1L, new BigDecimal("10.00"), null);
	}
}
