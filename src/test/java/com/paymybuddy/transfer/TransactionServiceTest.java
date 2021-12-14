package com.paymybuddy.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.TransactionRepository;
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

	@InjectMocks
	private TransactionService transactionService;

	private Wallet sender;

	private Wallet receiver;

	private WalletLink link;

	@BeforeEach
	void setUp() throws Exception {
		sender = Wallet.builder().amount(new BigDecimal(1000)).currency("EUR").id(1L).build();
		receiver = Wallet.builder().amount(BigDecimal.ZERO).currency("EUR").id(2L).build();
		link = WalletLink.builder().id(1L).name("nawak").sender(sender).receiver(receiver).build();
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.of(link));
	}

	@Test
	void makeTransaction_whenCalled_returnCorrectTransaction()
			throws InsufficientFundException, WrongUserException, EntityMissingException {
		Transaction test = transactionService.makeTransaction(1, new BigDecimal(100), "blablacar");

		assertThat(test.getAmount()).isEqualTo(new BigDecimal(100).setScale(2));
		assertThat(test.getFee()).isEqualTo(new BigDecimal(0.5).setScale(2));
		assertThat(test.getDescription()).isEqualTo("blablacar");
		assertThat(test.getLink()).isEqualTo(link);
	}

	@Test
	void makeTransaction_whenSenderDontHaveEnoughFund_throwsInsufficientFundException()
			throws InsufficientFundException, WrongUserException {
		assertThrows(InsufficientFundException.class,
				() -> transactionService.makeTransaction(1, new BigDecimal(1000), null));
	}

	@Test
	void makeTransaction_whenCalled_properlyTransferFunds() throws EntityMissingException, InsufficientFundException {
		transactionService.makeTransaction(1, new BigDecimal(100), null);

		assertThat(sender.getAmount()).isEqualTo(new BigDecimal(899.5).setScale(2));
		assertThat(receiver.getAmount()).isEqualTo(new BigDecimal(100).setScale(2));
	}

	@Test
	void makeTransaction_whenCalled_updateDatabase() throws EntityMissingException, InsufficientFundException {
		transactionService.makeTransaction(1, new BigDecimal(100), null);

		verify(mockTransactionRepository, times(1)).save(any(Transaction.class));
		verify(mockWalletRepository, times(2)).save(any(Wallet.class));
	}

	@Test
	void makeTransaction_whenCalledWithANegativeAmount_throwIllegalArgumentException()
			throws EntityMissingException, InsufficientFundException {
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.makeTransaction(1, new BigDecimal(-1001000), null));
	}

	@Test
	void findWalletLinkById_whenWalletLinkIdNotExist_throwsEntityMissingException() {
		when(mockWalletLinkRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(EntityMissingException.class, () -> transactionService.findWalletLinkById(1L));
	}

	@Test
	void findWalletLinkById_whenCalled_returnCorrectWalletLink() throws EntityMissingException {
		WalletLink test = transactionService.findWalletLinkById(1L);

		assertThat(test).isEqualTo(link);
	}

}
