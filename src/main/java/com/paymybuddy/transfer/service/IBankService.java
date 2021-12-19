package com.paymybuddy.transfer.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.BankCoordinate;
import com.paymybuddy.transfer.model.BankTransaction;

@Service
public interface IBankService {

	boolean linkUserToBankCoordinate(String userEmail, String accountNumber) throws EntityMissingException;

	BankCoordinate createBankCoordinate(String accountNumber) throws InvalidArgumentException;

	BankTransaction withdraw(String userEmail, long bankCoordinateId, long walletId, BigDecimal amount)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException;

	BankTransaction fund(String userEmail, long bankCoordinateId, long walletId, BigDecimal amount)
			throws EntityMissingException, WrongUserException, InvalidArgumentException;

}