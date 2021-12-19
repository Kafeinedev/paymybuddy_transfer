package com.paymybuddy.transfer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import com.paymybuddy.transfer.controller.WalletLinkController;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.service.WalletLinkService;

@WebMvcTest(WalletLinkController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "a@dress.com")
public class WalletLinkControllerTest {

	@MockBean
	private WalletLinkService mockWalletLinkService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createWalletLink_whenCalled_return2xxAndCreatedWalletLink() throws Exception {
		WalletLink link = new WalletLink();
		when(mockWalletLinkService.createWalletLink("name", "a@dress.com", 1, 2)).thenReturn(link);

		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "1").param("receiverId", "2")).andExpect(status().is2xxSuccessful())
				.andExpect(content()
						.string("{\"id\":0,\"name\":null,\"sender\":null,\"receiver\":null,\"transactions\":null}"));
	}

	@Test
	void createWalletLink_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(post("/walletlink").param("name", "name").param("senderId", "1").param("receiverId", "2"))
				.andExpect(status().isForbidden());
	}

	@Test
	void createWalletLink_whenCalled_useService() throws Exception {
		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "1").param("receiverId", "2"));

		verify(mockWalletLinkService, times(1)).createWalletLink("name", "a@dress.com", 1, 2);
	}

	@Test
	void createWalletLink_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockWalletLinkService.createWalletLink("name", "a@dress.com", 1, 2))
				.thenThrow(new InvalidArgumentException());

		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "1").param("receiverId", "2")).andExpect(status().isBadRequest());
	}

	@Test
	void createWalletLink_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockWalletLinkService.createWalletLink("name", "a@dress.com", 1, 2))
				.thenThrow(new EntityMissingException());

		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "1").param("receiverId", "2")).andExpect(status().isNotFound());
	}

	@Test
	void createWalletLink_whenServiceWrongUserException_return4xxUnauthorized() throws Exception {
		when(mockWalletLinkService.createWalletLink("name", "a@dress.com", 1, 2)).thenThrow(new WrongUserException());

		mockMvc.perform(post("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf()).param("name", "name")
				.param("senderId", "1").param("receiverId", "2")).andExpect(status().isUnauthorized());
	}

	@Test
	void updateWalletLinkName_whenCalled_return2xxAndCreatedWalletLink() throws Exception {
		WalletLink link = new WalletLink();
		when(mockWalletLinkService.updateName(1L, "name")).thenReturn(link);

		mockMvc.perform(patch("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("walletLinkId", "1").param("newName", "name")).andExpect(status().is2xxSuccessful())
				.andExpect(content()
						.string("{\"id\":0,\"name\":null,\"sender\":null,\"receiver\":null,\"transactions\":null}"));
	}

	@Test
	void updateWalletLinkName_whenCalletWithoutcsrfToken_return4xxForbidden() throws Exception {
		mockMvc.perform(patch("/walletlink").param("walletLinkId", "1").param("newName", "name"))
				.andExpect(status().isForbidden());
	}

	@Test
	void updateWalletLinkName_whenCalled_useService() throws Exception {
		mockMvc.perform(patch("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("walletLinkId", "1").param("newName", "name"));

		verify(mockWalletLinkService, times(1)).updateName(1L, "name");
	}

	@Test
	void updateWalletLinkName_whenServiceThrowInvalidArgumentsException_return4xxBadRequest() throws Exception {
		when(mockWalletLinkService.updateName(1L, "name")).thenThrow(new InvalidArgumentException());

		mockMvc.perform(patch("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("walletLinkId", "1").param("newName", "name")).andExpect(status().isBadRequest());
	}

	@Test
	void updateWalletLinkName_whenServiceThrowEntityMissingException_return4xxNotFound() throws Exception {
		when(mockWalletLinkService.updateName(1L, "name")).thenThrow(new EntityMissingException());

		mockMvc.perform(patch("/walletlink").with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("walletLinkId", "1").param("newName", "name")).andExpect(status().isNotFound());
	}
}
