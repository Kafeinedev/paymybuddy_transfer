package com.paymybuddy.transfer.service;

import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.RegexStringPattern;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.BankCoordinate;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.repository.BankCoordinateRepository;
import com.paymybuddy.transfer.repository.BankTransactionRepository;
import com.paymybuddy.transfer.repository.UserRepository;

@Service
public class DummyBankService {

	@Autowired
	private BankCoordinateRepository bankCoordinateRepository;

	@Autowired
	private UserRepository userRepository;

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

	private boolean validateAccountNumber(String accountNumber) throws InvalidArgumentException {
		if (!Pattern.matches(RegexStringPattern.IBAN, accountNumber)) {
			log.error("Could not create new BankCoordinate " + accountNumber + " is invalid");
			throw new InvalidArgumentException();
		}
		return true;
	}

}
