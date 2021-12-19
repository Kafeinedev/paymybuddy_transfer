package com.paymybuddy.transfer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.transfer.controller.BankController;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.BankCoordinate;
import com.paymybuddy.transfer.model.BankTransaction;
import com.paymybuddy.transfer.service.IBankService;

@WebMvcTest(BankController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "a@dress.com")
class BankControllerTest {

	@MockBean
	private IBankService mockBankService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void fund_whenCalled_return2xxAndCreatedBankTransaction() throws Exception {
		BankTransaction bTransaction = new BankTransaction();
		bTransaction.setDate(null);
		when(mockBankService.fund("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenReturn(bTransaction);

		mockMvc.perform(post("/fund").with(SecurityMockMvcRequestPostProcessors.csrf()).param("bankCoordinateId", "1")
				.param("walletId", "2").param("amount", "0")).andExpect(status().is2xxSuccessful())
				.andExpect(content().string(
						"{\"id\":0,\"amount\":null,\"date\":null,\"type\":null,\"bankCoordinate\":null,\"wallet\":null}"));
	}

	@Test
	void fund_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/fund").param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().isForbidden());
	}

	@Test
	void fund_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/fund").with(SecurityMockMvcRequestPostProcessors.csrf()).param("bankCoordinateId", "1")
				.param("walletId", "2").param("amount", "0"));

		verify(mockBankService, times(1)).fund("a@dress.com", 1L, 2L, BigDecimal.ZERO);
	}

	@Test
	void fund_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockBankService.fund("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenThrow(new InvalidArgumentException());

		mockMvc.perform(post("/fund").with(SecurityMockMvcRequestPostProcessors.csrf()).param("bankCoordinateId", "1")
				.param("walletId", "2").param("amount", "0")).andExpect(status().isBadRequest());
	}

	@Test
	void fund_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockBankService.fund("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenThrow(new EntityMissingException());

		mockMvc.perform(post("/fund").with(SecurityMockMvcRequestPostProcessors.csrf()).param("bankCoordinateId", "1")
				.param("walletId", "2").param("amount", "0")).andExpect(status().isNotFound());
	}

	@Test
	void fund_whenServiceThrowWrongUserException_return4xxUnauthorized() throws Exception {
		when(mockBankService.fund("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenThrow(new WrongUserException());

		mockMvc.perform(post("/fund").with(SecurityMockMvcRequestPostProcessors.csrf()).param("bankCoordinateId", "1")
				.param("walletId", "2").param("amount", "0")).andExpect(status().isUnauthorized());
	}

	@Test
	void withdraw_whenCalled_return2xxAndCreatedBankTransaction() throws Exception {
		BankTransaction bTransaction = new BankTransaction();
		bTransaction.setDate(null);
		when(mockBankService.withdraw("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenReturn(bTransaction);

		mockMvc.perform(post("/withdraw").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().is2xxSuccessful()).andExpect(content().string(
						"{\"id\":0,\"amount\":null,\"date\":null,\"type\":null,\"bankCoordinate\":null,\"wallet\":null}"));
	}

	@Test
	void withdraw_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/withdraw").param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().isForbidden());
	}

	@Test
	void withdraw_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/withdraw").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"));

		verify(mockBankService, times(1)).withdraw("a@dress.com", 1L, 2L, BigDecimal.ZERO);
	}

	@Test
	void withdraw_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockBankService.withdraw("a@dress.com", 1L, 2L, BigDecimal.ZERO))
				.thenThrow(new InvalidArgumentException());

		mockMvc.perform(post("/withdraw").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void withdraw_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockBankService.withdraw("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenThrow(new EntityMissingException());

		mockMvc.perform(post("/withdraw").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().isNotFound());
	}

	@Test
	void withdraw_whenServiceThrowWrongUserException_return4xxUnauthorized() throws Exception {
		when(mockBankService.withdraw("a@dress.com", 1L, 2L, BigDecimal.ZERO)).thenThrow(new WrongUserException());

		mockMvc.perform(post("/withdraw").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void withdraw_whenServiceThrowInsufficientFundException_return4xxBadRequest() throws Exception {
		when(mockBankService.withdraw("a@dress.com", 1L, 2L, BigDecimal.ZERO))
				.thenThrow(new InsufficientFundException());

		mockMvc.perform(post("/withdraw").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinateId", "1").param("walletId", "2").param("amount", "0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createBankCoordinate_whenCalled_return2xxAndCreatedBankCoordinate() throws Exception {
		BankCoordinate coordinate = new BankCoordinate();
		when(mockBankService.createBankCoordinate("numbers...")).thenReturn(coordinate);

		mockMvc.perform(post("/bankcoordinate").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinate", "numbers...")).andExpect(status().is2xxSuccessful()).andExpect(
						content().string("{\"id\":0,\"accountNumber\":null,\"bankTransactions\":null,\"users\":null}"));
	}

	@Test
	void createBankCoordinate_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/bankcoordinate").param("bankCoordinate", "numbers..."))
				.andExpect(status().isForbidden());
	}

	@Test
	void createBankCoordinate_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/bankcoordinate").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinate", "numbers..."));

		verify(mockBankService, times(1)).createBankCoordinate("numbers...");
	}

	@Test
	void createBankCoordinate_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockBankService.createBankCoordinate("numbers...")).thenThrow(new InvalidArgumentException());

		mockMvc.perform(post("/bankcoordinate").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinate", "numbers...")).andExpect(status().isBadRequest());
	}

	@Test
	void linkBankCoordinateToUser_whenCalled_return2xxAndSuccessStatus() throws Exception {
		when(mockBankService.linkUserToBankCoordinate("a@dress.com", "numbers...")).thenReturn(true);

		mockMvc.perform(post("/linkbankcoordinatetouser").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinate", "numbers...")).andExpect(status().is2xxSuccessful())
				.andExpect(content().string("true"));
	}

	@Test
	void linkBankCoordinateToUser_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/linkbankcoordinatetouser").param("bankCoordinate", "numbers..."))
				.andExpect(status().isForbidden());
	}

	@Test
	void linkBankCoordinateToUser_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/linkbankcoordinatetouser").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinate", "numbers..."));

		verify(mockBankService, times(1)).linkUserToBankCoordinate("a@dress.com", "numbers...");
	}

	@Test
	void linkBankCoordinateToUser_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockBankService.linkUserToBankCoordinate("a@dress.com", "numbers..."))
				.thenThrow(new EntityMissingException());

		mockMvc.perform(post("/linkbankcoordinatetouser").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("bankCoordinate", "numbers...")).andExpect(status().isNotFound());
	}
}
