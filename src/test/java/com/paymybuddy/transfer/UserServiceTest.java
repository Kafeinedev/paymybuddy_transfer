package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.paymybuddy.transfer.constant.PageSize;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.TransactionRepository;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.repository.WalletLinkRepository;
import com.paymybuddy.transfer.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private WalletLinkRepository mockWalletLinkRepository;

	@Mock
	private TransactionRepository mockTransactionRepository;

	@Mock
	private UserRepository mockUserRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void getAllOutgoingLinksByUserEmail_whenNoLinksExist_returnEmptyList() {
		when(mockWalletLinkRepository.findBySenderOwnerEmail("Idont@Exist.com"))
				.thenReturn(new ArrayList<WalletLink>());

		List<WalletLink> test = userService.getAllOutgoingLinksByUserEmail("Idont@Exist.com");

		assertThat(test).isEmpty();
	}

	@Test
	void getAllOutgoingLinksByUserEmail_whenLinksExist_returnProperListOfWalletLinks() {
		WalletLink link = WalletLink.builder().id(1L).name("name").sender(new Wallet()).receiver(new Wallet()).build();
		when(mockWalletLinkRepository.findBySenderOwnerEmail("I@Exist.com"))
				.thenReturn(new ArrayList<WalletLink>(List.of(link)));

		List<WalletLink> test = userService.getAllOutgoingLinksByUserEmail("I@Exist.com");

		assertThat(test.size()).isEqualTo(1);
		assertThat(test.get(0)).isEqualTo(link);
	}

	@Test
	void getAllOutgoingLinksByUserEmail_whenCalled_accessDatabase() {
		userService.getAllOutgoingLinksByUserEmail("Ido@Nothing.com");

		verify(mockWalletLinkRepository, times(1)).findBySenderOwnerEmail("Ido@Nothing.com");
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenNoTransactionWasFound_returnPageWithoutContent() {
		Pageable page = PageRequest.of(0, PageSize.TRANSACTIONS_INFO);
		when(mockTransactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc("ThisMethodName@isFarTooLong.com",
				page)).thenReturn(new PageImpl<Transaction>(new ArrayList<Transaction>()));

		Page<String[]> test = userService.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", 0);

		assertThat(test.getContent()).isEmpty();
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenTransactionsWereFound_returnProperPageOfTransactionsInfo() {
		Pageable page = PageRequest.of(0, PageSize.TRANSACTIONS_INFO);
		Transaction one = Transaction.builder().amount(BigDecimal.ZERO).fee(BigDecimal.ZERO).id(1)
				.link(new WalletLink()).description("thisisadescription").build();
		when(mockTransactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc("ThisMethodName@isFarTooLong.com",
				page)).thenReturn(new PageImpl<Transaction>(List.of(one)));
		when(mockWalletLinkRepository.findByTransactions(one)).thenReturn(Optional.of(WalletLink.builder().id(1L)
				.name("linkName").sender(Wallet.builder().currency("EUR").build()).receiver(new Wallet()).build()));

		Page<String[]> test = userService.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", 0);

		assertThat(test.getNumberOfElements()).isEqualTo(1);
		assertThat(test.getTotalPages()).isEqualTo(1);
		String[] contentTest = test.getContent().get(0);
		assertThat(contentTest[0]).isEqualTo("linkName");
		assertThat(contentTest[1]).isEqualTo("thisisadescription");
		assertThat(contentTest[2]).isEqualTo("0€");
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenCalled_accessDatabase() {
		Pageable page = PageRequest.of(0, PageSize.TRANSACTIONS_INFO);
		Transaction one = Transaction.builder().amount(BigDecimal.ZERO).fee(BigDecimal.ZERO).id(1)
				.link(new WalletLink()).description("thisisadescription").build();
		Transaction two = Transaction.builder().amount(BigDecimal.ZERO).fee(BigDecimal.ZERO).id(1)
				.link(new WalletLink()).description("thisisadescription").build();
		when(mockTransactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc("ThisMethodName@isFarTooLong.com",
				page)).thenReturn(new PageImpl<Transaction>(List.of(one, two)));
		when(mockWalletLinkRepository.findByTransactions(one)).thenReturn(Optional.of(WalletLink.builder().id(1L)
				.name("linkName").sender(Wallet.builder().currency("EUR").build()).receiver(new Wallet()).build()));

		userService.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", 0);

		verify(mockTransactionRepository, times(1)).findByLinkSenderOwnerEmailOrderByDateDesc(any(String.class),
				any(Pageable.class));
		verify(mockWalletLinkRepository, times(2)).findByTransactions(any(Transaction.class));
	}

	@Test
	void loadUserByUsername_whenUserEmailIsFound_returnProperUserDetails() {
		when(mockUserRepository.findByEmail("this is an email")).thenReturn(Optional.of(
				User.builder().email("this is an email").id(1L).password("totally encrypted").name("name").build()));

		UserDetails test = userService.loadUserByUsername("this is an email");

		assertThat(test.getAuthorities().size()).isEqualTo(1);
		assertThat(test.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))).isTrue();
		assertThat(test.getUsername()).isEqualTo("this is an email");
		assertThat(test.getPassword()).isEqualTo("totally encrypted");
		assertThat(test.isAccountNonExpired()).isTrue();
		assertThat(test.isAccountNonLocked()).isTrue();
		assertThat(test.isCredentialsNonExpired()).isTrue();
		assertThat(test.isEnabled()).isTrue();
	}

	@Test
	void loadUserByUsername_whenUserEmailIsNotFound_throwUsernameNotFoundException() {
		when(mockUserRepository.findByEmail("this is an email")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("this is an email"));
	}

	@Test
	void loadUserByUsername_whenCalled_accessDatabase() {
		when(mockUserRepository.findByEmail("this is an email")).thenReturn(Optional.of(
				User.builder().email("this is an email").id(1L).password("totally encrypted").name("name").build()));

		userService.loadUserByUsername("this is an email");

		verify(mockUserRepository, times(1)).findByEmail("this is an email");
	}
}
