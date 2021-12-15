package com.paymybuddy.transfer.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.Fee;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.TransactionRepository;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.repository.WalletLinkRepository;
import com.paymybuddy.transfer.repository.WalletRepository;

@Service
public class TransactionService {

	private Logger log = LogManager.getLogger("Transaction Service");

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private WalletLinkRepository walletLinkRepository;

	@Autowired
	private UserRepository userRepository;

	public Transaction makeTransaction(String emitterEmail, long walletLinkId, BigDecimal amount, String description)
			throws EntityMissingException, InsufficientFundException, WrongUserException {

		WalletLink link = walletLinkRepository.findById(walletLinkId).orElseThrow(() -> {
			log.error("Could not find walletLink with id : " + walletLinkId);
			return new EntityMissingException();
		});
		Wallet sender = link.getSender();
		Wallet receiver = link.getReceiver();
		BigDecimal fee = feeCalculation(amount);

		Transaction transaction = Transaction.builder().amount(amount.setScale(2, RoundingMode.HALF_UP)).link(link)
				.fee(fee).build();
		if (description != null) {
			transaction.setDescription(description);
		}

		if (verifyTransaction(emitterEmail, sender, transaction)) {
			saveToDatabase(sender, receiver, transaction);
		}
		return transaction;
	}

	private boolean verifyTransaction(String emitterEmail, Wallet sender, Transaction transaction)
			throws InsufficientFundException, WrongUserException, EntityMissingException {
		User emitter = userRepository.findByEmail(emitterEmail).orElseThrow(() -> {
			log.error("Could not find user with email : " + emitterEmail);
			return new EntityMissingException();
		});
		if (!sender.getOwner().equals(emitter)) {
			log.error("User : " + emitter.getId() + " trying to make a transaction from a wallet it doesnt own");
			throw new WrongUserException();
		}
		if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			log.error("Trying to make a transaction with a negative amount");
			throw new IllegalArgumentException();
		}
		if (sender.getAmount().compareTo(transaction.getAmount().add(transaction.getFee())) < 0) {
			log.error("Trying to make transaction without enough funds");
			throw new InsufficientFundException();
		}
		return true;
	}

	@Transactional
	private void saveToDatabase(Wallet sender, Wallet receiver, Transaction transaction) {
		sender.setAmount(sender.getAmount().subtract(transaction.getAmount().add(transaction.getFee())).setScale(2,
				RoundingMode.HALF_UP));
		receiver.setAmount(receiver.getAmount().add(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP));

		walletRepository.save(sender);
		walletRepository.save(receiver);
		transactionRepository.save(transaction);
	}

	private BigDecimal feeCalculation(BigDecimal amount) {
		return amount.multiply(Fee.STANDARD_FEE).setScale(2, RoundingMode.HALF_UP);
	}
}
