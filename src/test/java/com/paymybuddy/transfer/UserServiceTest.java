package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
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
	private PasswordEncoder mockPasswordEncoder;

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

	@Test
	void createUser_whenCalled_returnCreatedUser() throws Exception {
		User user = User.builder().email("email@a.com").id(1L).password("totally encrypted").name("name").build();
		when(mockUserRepository.save(any(User.class))).thenReturn(user);

		User test = userService.createUser("name", "email@a.com", "P@ssword1");

		assertThat(test.getId()).isEqualTo(1L);
		assertThat(test.getName()).isEqualTo("name");
		assertThat(test.getEmail()).isEqualTo("email@a.com");
		assertThat(test.getPassword()).isEqualTo("totally encrypted");
	}

	@Test
	void createUser_whenCalled_UpdateDatabase() throws Exception {
		when(mockPasswordEncoder.encode("P@ssword1")).thenReturn("totally encrypted");

		userService.createUser("name", "email@a.com", "P@ssword1");

		verify(mockUserRepository, times(1))
				.save(User.builder().id(0).email("email@a.com").password("totally encrypted").name("name").build());
	}

	@Test
	void createUser_whenCalled_encryptPassword() throws Exception {
		userService.createUser("name", "email@a.com", "P@ssword1");

		verify(mockPasswordEncoder, times(1)).encode("P@ssword1");
	}

	@Test
	void createUser_whenUsernameIsTooLong_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class,
				() -> userService.createUser("name iiiiiiiiiiiiiiiiiiiiiiiiiiiissssssss LOOOOOOOOOOOOOOOOONG !",
						"a@a.com", "P@ssword1"));
	}

	@Test
	void createUser_whenEmailIsInvalid_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> userService.createUser("name", "invalid", "P@ssword1"));
	}

	@Test
	void createUser_whenPasswordIsInvalid_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> userService.createUser("name", "mail@a.com", "123456"));
	}

	@Test
	void createUser_ifEmailAlreadyExist_throwInvalidArgumentException() {
		when(mockUserRepository.findByEmail("mail@a.com")).thenReturn(Optional.of(new User()));

		assertThrows(InvalidArgumentException.class, () -> userService.createUser("name", "mail@a.com", "P@ssword1"));
	}

	@Test
	void updateUser_whenCalled_returnUpdatedUser() throws Exception {
		User user = new User();

		when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user))
				.thenReturn(Optional.empty());
		when(mockUserRepository.save(user)).thenReturn(user);
		when(mockPasswordEncoder.encode("newP@ssword1")).thenReturn("encrypted");

		User test = userService.updateUser("email@a.com", "new name", "newmail@mail.com", "newP@ssword1");

		assertThat(test.getName()).isEqualTo("new name");
		assertThat(test.getEmail()).isEqualTo("newmail@mail.com");
		assertThat(test.getPassword()).isEqualTo("encrypted");
	}

	@Test
	void updateUser_whenCalledWithaNewPassword_encryptPassword() throws Exception {
		User user = new User();

		when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

		userService.updateUser("email@a.com", null, null, "newP@ssword1");

		verify(mockPasswordEncoder, times(1)).encode("newP@ssword1");
	}

	@Test
	void updateUser_whenCalled_checkAndUpdateDatabase() throws Exception {
		User user = new User();

		when(mockUserRepository.findByEmail("mel")).thenReturn(Optional.of(user));

		userService.updateUser("mel", "newname", "newmel@a.com", "P@ssWord1");

		verify(mockUserRepository, times(2)).findByEmail(any(String.class));
		verify(mockUserRepository, times(1)).save(user);
	}

	@Test
	void updateUser_ifMissingUser_throwEntityMissingException() {
		when(mockUserRepository.findByEmail("mail")).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> userService.updateUser("mail", null, null, null));
	}

	@Test
	void updateUser_whenUsernameIsTooLong_throwInvalidArgumentException() {
		when(mockUserRepository.findByEmail("toUpdate")).thenReturn(Optional.of(new User()));

		assertThrows(InvalidArgumentException.class, () -> userService.updateUser("toUpdate",
				"name iiiiiiiiiiiiiiiiiiiiiiiiiiiissssssss LOOOOOOOOOOOOOOOOONG !", "a@a.com", "P@ssword1"));
	}

	@Test
	void updateUser_whenEmailIsInvalid_throwInvalidArgumentException() {
		when(mockUserRepository.findByEmail("toUpdate")).thenReturn(Optional.of(new User()));

		assertThrows(InvalidArgumentException.class,
				() -> userService.updateUser("toUpdate", "name", "invalid.com", "P@ssword1"));
	}

	@Test
	void updateUser_whenPasswordIsInvalid_throwInvalidArgumentException() {
		when(mockUserRepository.findByEmail("toUpdate")).thenReturn(Optional.of(new User()));

		assertThrows(InvalidArgumentException.class,
				() -> userService.updateUser("toUpdate", "name", "a@a.com", "123456"));
	}

	@Test
	void updateUser_ifEmailAlreadyExist_throwInvalidArgumentException() {
		when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));

		assertThrows(InvalidArgumentException.class,
				() -> userService.updateUser("toUpdate", "name", "a@a.com", "P@ssword1"));
	}

}
