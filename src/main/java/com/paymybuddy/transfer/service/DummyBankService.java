package com.paymybuddy.transfer.service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.BankTransactionType;
import com.paymybuddy.transfer.constant.RegexStringPattern;
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

@Service
public class DummyBankService {

	@Autowired
	private BankCoordinateRepository bankCoordinateRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private BankTransactionRepository bankTransactionRepository;

	private Logger log = LogManager.getLogger("Bank Service");

	@Transactional
	public boolean linkUserToBankCoordinate(String userEmail, String accountNumber) throws EntityMissingException {
		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
			log.error("Could not link user to bankCoordinate user with email : " + userEmail + " not found");
			return new EntityMissingException();
		});
		BankCoordinate coordinate = bankCoordinateRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
			log.error("Could not link user to bankCoordinate accountNumber : " + accountNumber + " not found");
			return new EntityMissingException();
		});

		coordinate.getUsers().add(user);
		bankCoordinateRepository.save(coordinate);
		return true;
	}

	@Transactional
	public BankCoordinate createBankCoordinate(String accountNumber) throws InvalidArgumentException {
		validateAccountNumber(accountNumber);
		if (bankCoordinateRepository.findByAccountNumber(accountNumber).isPresent()) {
			log.error("Could not create new BankCoordinate : " + accountNumber + " already exist");
			throw new InvalidArgumentException();
		}

		BankCoordinate coordinate = BankCoordinate.builder().accountNumber(accountNumber).build();
		return bankCoordinateRepository.save(coordinate);
	}

	@Transactional
	public BankTransaction withdraw(String userEmail, long bankCoordinateId, long walletId, BigDecimal amount)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException {
		BankCoordinate coordinate = bankCoordinateRepository.findById(bankCoordinateId).orElseThrow(() -> {
			log.error("Could not execute withdrawal bankCoordinate id : " + bankCoordinateId + " not found");
			return new EntityMissingException();
		});
		Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> {
			log.error("Could not execute withdrawal wallet id : " + walletId + " not found");
			return new EntityMissingException();
		});

		BankTransaction bTransaction = BankTransaction.builder().amount(amount).type(BankTransactionType.WITHDRAWAL)
				.bankCoordinate(coordinate).wallet(wallet).build();
		if (verifyWithdrawal(userEmail, bTransaction)) {
			wallet.setAmount(wallet.getAmount().subtract(amount).setScale(2));
			walletRepository.save(wallet);
			bankTransactionRepository.save(bTransaction);
		}
		return bTransaction;
	}

	@Transactional
	public BankTransaction fund(String userEmail, long bankCoordinateId, long walletId, BigDecimal amount)
			throws EntityMissingException, WrongUserException, InvalidArgumentException {
		BankCoordinate coordinate = bankCoordinateRepository.findById(bankCoordinateId).orElseThrow(() -> {
			log.error("Could not execute funding bankCoordinate id : " + bankCoordinateId + " not found");
			return new EntityMissingException();
		});
		Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> {
			log.error("Could not execute funding wallet id : " + walletId + " not found");
			return new EntityMissingException();
		});
		BankTransaction bTransaction = BankTransaction.builder().amount(amount).type(BankTransactionType.FUND)
				.bankCoordinate(coordinate).wallet(wallet).build();
		if (verifyFunding(userEmail, bTransaction)) {
			wallet.setAmount(wallet.getAmount().add(amount).setScale(2));
			walletRepository.save(wallet);
			bankTransactionRepository.save(bTransaction);
		}
		return bTransaction;
	}

	private boolean verifyFunding(String userEmail, BankTransaction funding)
			throws WrongUserException, InvalidArgumentException {
		Wallet wallet = funding.getWallet();
		User user = wallet.getOwner();

		if (!user.getBankCoordinates().contains(funding.getBankCoordinate())) {
			log.error("User with userEmail " + userEmail + "trying to fund from an account he isnt linked to");
			throw new InvalidArgumentException();
		}
		if (funding.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			log.error("User with userEmail " + userEmail + "trying to fund with a negative amount");
			throw new InvalidArgumentException();
		}
		if (!user.getEmail().equals(userEmail)) {
			log.error("User with userEmail " + userEmail + "trying to fund a wallet he doesnt own");
			throw new WrongUserException();
		}
		return true;
	}

	private boolean validateAccountNumber(String accountNumber) throws InvalidArgumentException {
		if (!Pattern.matches(RegexStringPattern.IBAN, accountNumber)) {
			log.error("Could not create new BankCoordinate " + accountNumber + " is invalid");
			throw new InvalidArgumentException();
		}
		return true;
	}

	private boolean verifyWithdrawal(String userEmail, BankTransaction withdrawal)
			throws InsufficientFundException, WrongUserException, InvalidArgumentException {
		Wallet wallet = withdrawal.getWallet();
		User user = wallet.getOwner();

		if (!user.getBankCoordinates().contains(withdrawal.getBankCoordinate())) {
			log.error("User with userEmail " + userEmail + "trying to withdraw toward an account he isnt linked to");
			throw new InvalidArgumentException();
		}
		if (withdrawal.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			log.error("User with userEmail " + userEmail + "trying to withdraw a negative amount");
			throw new InvalidArgumentException();
		}
		if (!user.getEmail().equals(userEmail)) {
			log.error("User with userEmail " + userEmail + "trying to withdraw from a wallet he doesnt own");
			throw new WrongUserException();
		}
		if (wallet.getAmount().compareTo(withdrawal.getAmount()) < 0) {
			log.error("User with userEmail " + userEmail + "trying to withdraw more fund than what is in wallet");
			throw new InsufficientFundException();
		}
		return true;
	}

}
