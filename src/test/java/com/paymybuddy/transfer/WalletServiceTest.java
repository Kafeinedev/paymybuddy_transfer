package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.repository.IUserRepository;
import com.paymybuddy.transfer.repository.IWalletRepository;
import com.paymybuddy.transfer.service.WalletService;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

	@Mock
	private IWalletRepository mockWalletRepository;

	@Mock
	private IUserRepository mockUserRepository;

	@InjectMocks
	private WalletService walletService;

	private User owner;

	@BeforeEach
	void setUp() throws Exception {
		owner = User.builder().id(1L).name("name").email("mail").password("pass").build();
	}

	@Test
	void createWallet_whenCalled_returnCreatedWallet() throws Exception {
		when(mockWalletRepository.save(any(Wallet.class)))
				.thenReturn(Wallet.builder().currency("EUR").owner(owner).build());
		when(mockUserRepository.findByEmail("mail")).thenReturn(Optional.of(owner));

		Wallet test = walletService.createWallet("mail", "EUR");

		assertThat(test.getOwner()).isEqualTo(owner);
		assertThat(test.getCurrency()).isEqualTo("EUR");
	}

	@Test
	void createWallet_whenCalled_checkAndUpdateDatabase() throws Exception {
		when(mockUserRepository.findByEmail("mail")).thenReturn(Optional.of(owner));

		walletService.createWallet("mail", "EUR");

		verify(mockUserRepository, times(1)).findByEmail("mail");
		verify(mockWalletRepository, times(1)).save(any(Wallet.class));
	}

	@Test
	void createWallet_whenWrongCurrencyIsUsed_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> walletService.createWallet("mail", "DLS"));
	}

	@Test
	void createWallet_whenOnerIsNotFound_throwEntityMissingException() {
		when(mockUserRepository.findByEmail("mail")).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> walletService.createWallet("mail", "EUR"));
	}
}
