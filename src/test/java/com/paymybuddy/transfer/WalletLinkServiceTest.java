package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.WalletLinkRepository;
import com.paymybuddy.transfer.repository.WalletRepository;
import com.paymybuddy.transfer.service.impl.WalletLinkServiceImpl;

@ExtendWith(MockitoExtension.class)
class WalletLinkServiceTest {

	@Mock
	private WalletRepository mockWalletRepository;

	@Mock
	private WalletLinkRepository mockWalletLinkRepository;

	@InjectMocks
	private WalletLinkServiceImpl walletLinkService;

	private User senderOwner;

	private User receiverOwner;

	private Wallet sender;

	private Wallet receiver;

	@BeforeEach
	void setUp() throws Exception {
		senderOwner = User.builder().name("name").id(1L).password("pass").email("email").build();
		receiverOwner = User.builder().name("name").id(2L).password("pass").email("email2").build();
		sender = Wallet.builder().currency("EUR").owner(senderOwner).build();
		receiver = Wallet.builder().currency("EUR").owner(receiverOwner).build();
	}

	@Test
	void getAllOutgoingLinksByUserEmail_whenNoLinksExist_returnEmptyList() {
		when(mockWalletLinkRepository.findBySenderOwnerEmail("Idont@Exist.com"))
				.thenReturn(new ArrayList<WalletLink>());

		List<WalletLink> test = walletLinkService.getAllOutgoingLinksByUserEmail("Idont@Exist.com");

		assertThat(test).isEmpty();
	}

	@Test
	void getAllOutgoingLinksByUserEmail_whenLinksExist_returnProperListOfWalletLinks() {
		WalletLink link = WalletLink.builder().id(1L).name("name").sender(new Wallet()).receiver(new Wallet()).build();
		when(mockWalletLinkRepository.findBySenderOwnerEmail("I@Exist.com"))
				.thenReturn(new ArrayList<WalletLink>(List.of(link)));

		List<WalletLink> test = walletLinkService.getAllOutgoingLinksByUserEmail("I@Exist.com");

		assertThat(test.size()).isEqualTo(1);
		assertThat(test.get(0)).isEqualTo(link);
	}

	@Test
	void getAllOutgoingLinksByUserEmail_whenCalled_accessDatabase() {
		walletLinkService.getAllOutgoingLinksByUserEmail("Ido@Nothing.com");

		verify(mockWalletLinkRepository, times(1)).findBySenderOwnerEmail("Ido@Nothing.com");
	}

	@Test
	void createWalletLink_whenCalled_returnCreatedWalletLink()
			throws WrongUserException, InvalidArgumentException, EntityMissingException {
		when(mockWalletRepository.findById(any(Long.class))).thenReturn(Optional.of(sender))
				.thenReturn(Optional.of(receiver));
		when(mockWalletLinkRepository.save(WalletLink.builder().name("name").sender(sender).receiver(receiver).build()))
				.thenReturn(WalletLink.builder().id(1L).name("name").sender(sender).receiver(receiver).build());

		WalletLink test = walletLinkService.createWalletLink("name", "email", 5L, 6L);

		assertThat(test.getId()).isEqualTo(1L);
		assertThat(test.getName()).isEqualTo("name");
		assertThat(test.getSender()).isEqualTo(sender);
		assertThat(test.getReceiver()).isEqualTo(receiver);
	}

	@Test
	void createWalletLink_whenCalled_checkAndUpdateDatabase()
			throws WrongUserException, InvalidArgumentException, EntityMissingException {
		when(mockWalletRepository.findById(any(Long.class))).thenReturn(Optional.of(sender))
				.thenReturn(Optional.of(receiver));

		walletLinkService.createWalletLink("name", "email", 5L, 6L);

		verify(mockWalletRepository, times(2)).findById(any(Long.class));
		verify(mockWalletLinkRepository, times(1)).save(any(WalletLink.class));
	}

	@Test
	void createWalletLink_whenWrongUserTryToMakeAWalletLink_throwWrongUserException() {
		when(mockWalletRepository.findById(any(Long.class))).thenReturn(Optional.of(sender))
				.thenReturn(Optional.of(receiver));

		assertThrows(WrongUserException.class, () -> walletLinkService.createWalletLink("name", "wrongemail", 5L, 6L));
	}

	@Test
	void createWalletLink_whenTwoWalletWithDifferentCurrenciesAreGiven_throwInvalidArgumentException() {
		when(mockWalletRepository.findById(any(Long.class))).thenReturn(Optional.of(sender))
				.thenReturn(Optional.of(receiver));
		sender.setCurrency("DLS");

		assertThrows(InvalidArgumentException.class, () -> walletLinkService.createWalletLink("name", "email", 5L, 6L));
	}

	@Test
	void createWalletLink_whenAWalletIsNotFound_throwEntityMissingException() {
		when(mockWalletRepository.findById(any(Long.class))).thenReturn(Optional.of(sender))
				.thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> walletLinkService.createWalletLink("name", "email", 5L, 6L));

		when(mockWalletRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> walletLinkService.createWalletLink("name", "email", 5L, 6L));
	}

	@Test
	void createWalletLink_whenNameIsTooLong_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> walletLinkService
				.createWalletLink("Looooooooooooooooooooooooooooooooooooooongname", "email", 5L, 6L));
	}

	@Test
	void updateName_whenCalled_returnUpdatedWalletLink() throws Exception {
		WalletLink link = WalletLink.builder().id(1L).name("name").sender(sender).receiver(receiver).build();

		when(mockWalletLinkRepository.findById(1L)).thenReturn(Optional.of(link));
		when(mockWalletLinkRepository.save(link)).thenReturn(link);

		WalletLink test = walletLinkService.updateWalletLinkName(1L, "BetterName");

		assertThat(test.getName()).isEqualTo("BetterName");
		assertThat(test.getSender()).isEqualTo(sender);
		assertThat(test.getReceiver()).isEqualTo(receiver);
	}

	@Test
	void updateName_whenCalled_updateDatabase() throws Exception {
		WalletLink link = WalletLink.builder().id(1L).name("name").sender(sender).receiver(receiver).build();

		when(mockWalletLinkRepository.findById(1L)).thenReturn(Optional.of(link));

		walletLinkService.updateWalletLinkName(1L, "newname");

		verify(mockWalletLinkRepository, times(1)).save(link);
	}

	@Test
	void updateName_ifNameTooLong_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> walletLinkService.updateWalletLinkName(1L,
				"ThisNAMEE ISSSSSSSSSSSSSS farrrrrrrrrrrrrrrr tooooooooo loooooong"));
	}

	@Test
	void updateName_ifMissingWalletLink_throwEntityMissingException() {
		when(mockWalletLinkRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> walletLinkService.updateWalletLinkName(1L, "new name"));
	}
}
