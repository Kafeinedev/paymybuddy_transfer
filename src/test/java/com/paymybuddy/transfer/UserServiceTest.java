package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.paymybuddy.transfer.model.User;
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
