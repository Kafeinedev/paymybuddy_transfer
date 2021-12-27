package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.transfer.constant.BankTransactionType;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.BankCoordinate;
import com.paymybuddy.transfer.model.BankTransaction;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.repository.BankCoordinateRepository;
import com.paymybuddy.transfer.repository.BankTransactionRepository;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.repository.WalletRepository;
import com.paymybuddy.transfer.service.impl.DummyBankService;

@ExtendWith(MockitoExtension.class)
class DummyBankServiceTest {

	@Mock
	private BankCoordinateRepository mockBankCoordinateRepository;

	@Mock
	private BankTransactionRepository mockBankTransactionRepository;

	@Mock
	private WalletRepository mockWalletRepository;

	@Mock
	private UserRepository mockUserRepository;

	@InjectMocks
	private DummyBankService bankService;

	private User user;

	private Wallet wallet;

	private BankCoordinate bankCoordinate;

	@BeforeEach
	void setUp() throws Exception {
		bankCoordinate = BankCoordinate.builder().id(1L).accountNumber("number").build();
		user = User.builder().id(1L).name("name").email("email").password("pass").build();
		wallet = Wallet.builder().amount(BigDecimal.TEN).currency("EUR").owner(user).build();
		bankCoordinate.setUsers(List.of(user));
		user.setBankCoordinates(List.of(bankCoordinate));
	}

	@Test
	void createBankCoordinate_whenCalled_returnCreatedBankCoordinate() throws Exception {
		BankCoordinate coordinate = BankCoordinate.builder().id(1L).accountNumber("FR6217569000304586191889J72")
				.build();
		when(mockBankCoordinateRepository.save(any(BankCoordinate.class))).thenReturn(coordinate);

		BankCoordinate test = bankService.createBankCoordinate("FR6217569000304586191889J72");

		assertThat(test.getId()).isEqualTo(1L);
		assertThat(test.getAccountNumber()).isEqualTo("FR6217569000304586191889J72");
	}

	@Test
	void createBankCoordinate_whenCalled_UpdateDatabase() throws Exception {
		bankService.createBankCoordinate("FR6217569000304586191889J72");

		verify(mockBankCoordinateRepository, times(1))
				.save(BankCoordinate.builder().accountNumber("FR6217569000304586191889J72").build());
	}

	@Test
	void createBankCoordinate_whenBankCoordinateIsInvalid_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> bankService.createBankCoordinate("invalid"));
	}

	@Test
	void createBankCoordinate_ifAccountNumberAlreadyExist_throwInvalidArgumentException() {
		when(mockBankCoordinateRepository.findByAccountNumber("FR6217569000304586191889J72"))
				.thenReturn(Optional.of(new BankCoordinate()));

		assertThrows(InvalidArgumentException.class,
				() -> bankService.createBankCoordinate("FR6217569000304586191889J72"));
	}

	@Test
	void linkUserToBankCoordinate_whenCalled_returnTrueIfUserHasBeenLinked() throws EntityMissingException {
		when(mockBankCoordinateRepository.findByAccountNumber("number")).thenReturn(Optional.of(
				BankCoordinate.builder().id(1L).accountNumber("accountNumber").users(new ArrayList<User>()).build()));
		when(mockUserRepository.findByEmail("mail@mail.com")).thenReturn(
				Optional.of(User.builder().name("name").email("mail@mail.com").id(1L).password("pass").build()));
		boolean test = bankService.linkUserToBankCoordinate("mail@mail.com", "number");

		assertThat(test).isTrue();
	}

	@Test
	void linkUserToBankCoordinate_whenCalled_checkAndUpdateDatabase() throws EntityMissingException {
		when(mockBankCoordinateRepository.findByAccountNumber("number")).thenReturn(Optional.of(
				BankCoordinate.builder().id(1L).accountNumber("accountNumber").users(new ArrayList<User>()).build()));
		when(mockUserRepository.findByEmail("mail@mail.com")).thenReturn(
				Optional.of(User.builder().name("name").email("mail@mail.com").id(1L).password("pass").build()));
		bankService.linkUserToBankCoordinate("mail@mail.com", "number");

		verify(mockUserRepository, times(1)).findByEmail("mail@mail.com");
		verify(mockBankCoordinateRepository, times(1)).findByAccountNumber("number");
		verify(mockBankCoordinateRepository, times(1)).save(any(BankCoordinate.class));
	}

	@Test
	void linkUserToBankCoordinate_ifUserIsMissing_throwEntityMissingException() {
		when(mockUserRepository.findByEmail("email@mail.com")).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class,
				() -> bankService.linkUserToBankCoordinate("email@mail.com", "accountNumber"));
	}

	@Test
	void linkUserToBankCoordinate_ifBankTransactionIsMissing_throwEntityMissingException() {
		when(mockUserRepository.findByEmail("email@mail.com")).thenReturn(
				Optional.of(User.builder().name("name").email("mail@mail.com").id(1L).password("pass").build()));
		when(mockBankCoordinateRepository.findByAccountNumber("accountNumber")).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class,
				() -> bankService.linkUserToBankCoordinate("email@mail.com", "accountNumber"));
	}

	@Test
	void withdraw_whenCalled_returnCorrectBankTransaction() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		BankTransaction test = bankService.withdraw("email", 1L, 1L, BigDecimal.ONE);

		assertThat(test.getAmount()).isEqualTo(BigDecimal.ONE);
		assertThat(test.getBankCoordinate()).isEqualTo(bankCoordinate);
		assertThat(test.getWallet()).isEqualTo(wallet);
		assertThat(test.getType()).isEqualTo(BankTransactionType.WITHDRAWAL);
	}

	@Test
	void withdraw_whenCalled_properlySubstractFunds() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		bankService.withdraw("email", 1L, 1L, BigDecimal.ONE);

		assertThat(wallet.getAmount()).isEqualTo(new BigDecimal(9.00).setScale(2));
	}

	@Test
	void withdraw_whenCalled_updateDatabase() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		bankService.withdraw("email", 1L, 1L, BigDecimal.ONE);

		verify(mockWalletRepository, times(1)).save(wallet);
		verify(mockBankTransactionRepository, times(1)).save(any(BankTransaction.class));
	}

	@Test
	void withdraw_whenNotEnoughFundInWallet_throwsInsufficientFundException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		assertThrows(InsufficientFundException.class,
				() -> bankService.withdraw("email", 1L, 1L, new BigDecimal(10000)));
	}

	@Test
	void withdraw_whenCalledWithANegativeAmount_throwInvalidArgumentException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		assertThrows(InvalidArgumentException.class,
				() -> bankService.withdraw("email", 1L, 1L, new BigDecimal(-10000)));
	}

	@Test
	void withdraw_whenUserDontOwnWallet_throwWrongUserException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));
		wallet.getOwner().setEmail("oh nyo !");

		assertThrows(WrongUserException.class, () -> bankService.withdraw("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void withdraw_whenWalletDoesntExist_throwEntityMissingException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> bankService.withdraw("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void withdraw_whenUserDoesntHaveALinkToBankCoordinate_throwInvalidArgumentException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));
		wallet.getOwner().setBankCoordinates(new ArrayList<>());

		assertThrows(InvalidArgumentException.class, () -> bankService.withdraw("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void withdraw_whenBankCoordinateDoesntExist_throwEntityMissingException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> bankService.withdraw("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void fund_whenCalled_returnCorrectBankTransaction() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		BankTransaction test = bankService.fund("email", 1L, 1L, BigDecimal.ONE);

		assertThat(test.getAmount()).isEqualTo(BigDecimal.ONE);
		assertThat(test.getBankCoordinate()).isEqualTo(bankCoordinate);
		assertThat(test.getWallet()).isEqualTo(wallet);
		assertThat(test.getType()).isEqualTo(BankTransactionType.FUND);
	}

	@Test
	void fund_whenCalled_properlyAddsFunds() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		bankService.fund("email", 1L, 1L, BigDecimal.ONE);

		assertThat(wallet.getAmount()).isEqualTo(new BigDecimal(11.00).setScale(2));
	}

	@Test
	void fund_whenCalled_updateDatabase() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		bankService.fund("email", 1L, 1L, BigDecimal.ONE);

		verify(mockWalletRepository, times(1)).save(wallet);
		verify(mockBankTransactionRepository, times(1)).save(any(BankTransaction.class));
	}

	@Test
	void fund_whenCalledWithANegativeAmount_throwInvalidArgumentException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));

		assertThrows(InvalidArgumentException.class, () -> bankService.fund("email", 1L, 1L, new BigDecimal(-10000)));
	}

	@Test
	void fund_whenUserDontOwnWallet_throwWrongUserException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));
		wallet.getOwner().setEmail("oh nyo !");

		assertThrows(WrongUserException.class, () -> bankService.fund("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void fund_whenWalletDoesntExist_throwEntityMissingException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> bankService.fund("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void fund_whenUserDoesntHaveALinkToBankCoordinate_throwInvalidArgumentException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.of(bankCoordinate));
		when(mockWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));
		wallet.getOwner().setBankCoordinates(new ArrayList<>());

		assertThrows(InvalidArgumentException.class, () -> bankService.fund("email", 1L, 1L, BigDecimal.ONE));
	}

	@Test
	void fund_whenBankCoordinateDoesntExist_throwEntityMissingException() throws Exception {
		when(mockBankCoordinateRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> bankService.fund("email", 1L, 1L, BigDecimal.ONE));
	}
}
