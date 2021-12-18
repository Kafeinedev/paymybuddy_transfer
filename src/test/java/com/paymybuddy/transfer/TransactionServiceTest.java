package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.paymybuddy.transfer.constant.PageSize;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.repository.TransactionRepository;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.repository.WalletLinkRepository;
import com.paymybuddy.transfer.repository.WalletRepository;
import com.paymybuddy.transfer.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private TransactionRepository mockTransactionRepository;

	@Mock
	private WalletRepository mockWalletRepository;

	@Mock
	private WalletLinkRepository mockWalletLinkRepository;

	@Mock
	private UserRepository mockUserRepository;

	@InjectMocks
	private TransactionService transactionService;

	private Wallet sender;

	private Wallet receiver;

	private WalletLink link;

	@BeforeEach
	void setUp() throws Exception {
		sender = Wallet.builder().amount(new BigDecimal(1000)).currency("EUR").id(1L).build();
		sender.setOwner(User.builder().build());
		receiver = Wallet.builder().amount(BigDecimal.ZERO).currency("EUR").id(2L).build();
		link = WalletLink.builder().id(1L).name("nawak").sender(sender).receiver(receiver).build();
	}

	@Test
	void makeTransaction_whenCalled_returnCorrectTransaction() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(sender.getOwner()));

		Transaction test = transactionService.makeTransaction("email", 1, new BigDecimal(100), "blablacar");

		assertThat(test.getAmount()).isEqualTo(new BigDecimal(100).setScale(2));
		assertThat(test.getFee()).isEqualTo(new BigDecimal(0.5).setScale(2));
		assertThat(test.getDescription()).isEqualTo("blablacar");
		assertThat(test.getLink()).isEqualTo(link);
	}

	@Test
	void makeTransaction_whenSenderDontHaveEnoughFund_throwsInsufficientFundException() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(sender.getOwner()));

		assertThrows(InsufficientFundException.class,
				() -> transactionService.makeTransaction("email", 1, new BigDecimal(1000), null));
	}

	@Test
	void makeTransaction_whenCalled_properlyTransferFunds() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(sender.getOwner()));

		transactionService.makeTransaction("email", 1, new BigDecimal(100), null);

		assertThat(sender.getAmount()).isEqualTo(new BigDecimal(899.5).setScale(2));
		assertThat(receiver.getAmount()).isEqualTo(new BigDecimal(100).setScale(2));
	}

	@Test
	void makeTransaction_whenCalled_updateDatabase() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(sender.getOwner()));

		transactionService.makeTransaction("email", 1, new BigDecimal(100), null);

		verify(mockTransactionRepository, times(1)).save(any(Transaction.class));
		verify(mockWalletRepository, times(2)).save(any(Wallet.class));
	}

	@Test
	void makeTransaction_whenCalledWithANegativeAmount_throwInvalidArgumentException() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(sender.getOwner()));

		assertThrows(InvalidArgumentException.class,
				() -> transactionService.makeTransaction("email", 1, new BigDecimal(-1001000), null));
	}

	@Test
	void makeTransaction_whenUserWhoDontOwnSenderWalletMakeTransaction_throwWrongUserException() throws Exception {
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(User.builder().name("chipper").build()));
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));

		assertThrows(WrongUserException.class,
				() -> transactionService.makeTransaction("email", 1, new BigDecimal(10), null));
	}

	@Test
	void makeTransaction_whenUserDoesntExist_throwEntityMissingException() throws Exception {
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.empty());
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		assertThrows(EntityMissingException.class,
				() -> transactionService.makeTransaction("email", 1, new BigDecimal(10), null));
	}

	@Test
	void makeTransaction_whenWalletLinkDoesntExist_throwEntityMissingException() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class,
				() -> transactionService.makeTransaction("email", 1, new BigDecimal(10), null));
	}

	@Test
	void makeTransaction_whenCalledWithADescriptionThatIsTooLong_throwInvalidArgumentException() throws Exception {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
		when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(sender.getOwner()));

		assertThrows(InvalidArgumentException.class,
				() -> transactionService.makeTransaction("email", 1, new BigDecimal(0),
						"This description is far too loooooooooooooooooooooooooooooooooooooooooooooooooo"
								+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
								+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
								+ "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"));
	}

	@Test
	void updateDescription_whenCalled_returnUpdatedTransaction() throws Exception {
		Transaction transaction = Transaction.builder().link(link).amount(BigDecimal.TEN).fee(BigDecimal.ONE).id(1L)
				.build();
		Date dateTransaction = transaction.getDate();
		when(mockTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
		when(mockTransactionRepository.save(transaction)).thenReturn(transaction);

		Transaction test = transactionService.updateDescription(1L, "this is an updated description");

		assertThat(test.getDescription()).isEqualTo("this is an updated description");
		assertThat(test.getAmount()).isEqualTo(BigDecimal.TEN);
		assertThat(test.getFee()).isEqualTo(BigDecimal.ONE);
		assertThat(test.getDate()).isEqualTo(dateTransaction);
	}

	@Test
	void updateDescription_whenCalled_updateDatabase() throws Exception {
		Transaction transaction = Transaction.builder().link(link).amount(BigDecimal.TEN).fee(BigDecimal.ONE).id(1L)
				.build();
		when(mockTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

		transactionService.updateDescription(1L, "this is an updated description");

		verify(mockTransactionRepository, times(1)).save(transaction);
	}

	@Test
	void updateDescription_ifDescriptionTooLong_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> transactionService.updateDescription(1L,
				"This description is far too loooooooooooooooooooooooooooooooooooooooooooooooooo"
						+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
						+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
						+ "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"));
	}

	@Test
	void updateDescription_ifMissingTransaction_throwEntityMissingException() {
		when(mockTransactionRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class,
				() -> transactionService.updateDescription(1L, "this is an updated description"));
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenNoTransactionWasFound_returnPageWithoutContent()
			throws InvalidArgumentException {
		Pageable page = PageRequest.of(0, PageSize.TRANSACTIONS_INFO);
		when(mockTransactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc("ThisMethodName@isFarTooLong.com",
				page)).thenReturn(new PageImpl<Transaction>(new ArrayList<Transaction>()));

		Page<String[]> test = transactionService
				.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", 0);

		assertThat(test.getContent()).isEmpty();
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenTransactionsWereFound_returnProperPageOfTransactionsInfo()
			throws InvalidArgumentException {
		Pageable page = PageRequest.of(0, PageSize.TRANSACTIONS_INFO);
		Transaction one = Transaction.builder().amount(BigDecimal.ZERO).fee(BigDecimal.ZERO).id(1)
				.link(new WalletLink()).description("thisisadescription").build();
		when(mockTransactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc("ThisMethodName@isFarTooLong.com",
				page)).thenReturn(new PageImpl<Transaction>(List.of(one)));
		when(mockWalletLinkRepository.findByTransactions(one)).thenReturn(Optional.of(WalletLink.builder().id(1L)
				.name("linkName").sender(Wallet.builder().currency("EUR").build()).receiver(new Wallet()).build()));

		Page<String[]> test = transactionService
				.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", 0);

		assertThat(test.getNumberOfElements()).isEqualTo(1);
		assertThat(test.getTotalPages()).isEqualTo(1);
		String[] contentTest = test.getContent().get(0);
		assertThat(contentTest[0]).isEqualTo("linkName");
		assertThat(contentTest[1]).isEqualTo("thisisadescription");
		assertThat(contentTest[2]).isEqualTo("0€");
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenPageIsNegative_throwInvalidArgumentException() {
		assertThrows(InvalidArgumentException.class,
				() -> transactionService.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", -1));
	}

	@Test
	void getTransactionsInfoByUserEmailAndPage_whenCalled_accessDatabase() throws InvalidArgumentException {
		Pageable page = PageRequest.of(0, PageSize.TRANSACTIONS_INFO);
		Transaction one = Transaction.builder().amount(BigDecimal.ZERO).fee(BigDecimal.ZERO).id(1)
				.link(new WalletLink()).description("thisisadescription").build();
		Transaction two = Transaction.builder().amount(BigDecimal.ZERO).fee(BigDecimal.ZERO).id(1)
				.link(new WalletLink()).description("thisisadescription").build();
		when(mockTransactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc("ThisMethodName@isFarTooLong.com",
				page)).thenReturn(new PageImpl<Transaction>(List.of(one, two)));
		when(mockWalletLinkRepository.findByTransactions(one)).thenReturn(Optional.of(WalletLink.builder().id(1L)
				.name("linkName").sender(Wallet.builder().currency("EUR").build()).receiver(new Wallet()).build()));

		transactionService.getTransactionsInfoByUserEmailAndPage("ThisMethodName@isFarTooLong.com", 0);

		verify(mockTransactionRepository, times(1)).findByLinkSenderOwnerEmailOrderByDateDesc(any(String.class),
				any(Pageable.class));
		verify(mockWalletLinkRepository, times(2)).findByTransactions(any(Transaction.class));
	}
}
