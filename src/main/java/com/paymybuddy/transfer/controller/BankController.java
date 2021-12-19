package com.paymybuddy.transfer.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.BankCoordinate;
import com.paymybuddy.transfer.model.BankTransaction;
import com.paymybuddy.transfer.service.DummyBankService;

/*
 * Prototype bank controller designed to allow database modification without access to it
 */

@RestController
public class BankController {

	@Autowired
	private DummyBankService bankService;

	@PostMapping("/fund")
	public BankTransaction fund(long bankCoordinateId, long walletId, BigDecimal amount, Authentication auth)
			throws EntityMissingException, WrongUserException, InvalidArgumentException {
		return bankService.fund(auth.getName(), bankCoordinateId, walletId, amount);
	}

	@PostMapping("/withdraw")
	public BankTransaction withdraw(long bankCoordinateId, long walletId, BigDecimal amount, Authentication auth)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException {
		return bankService.withdraw(auth.getName(), bankCoordinateId, walletId, amount);
	}

	@PostMapping("/bankcoordinate")
	public BankCoordinate createBankCoordinate(String bankCoordinate) throws InvalidArgumentException {
		return bankService.createBankCoordinate(bankCoordinate);
	}

	@PostMapping("/linkbankcoordinatetouser")
	public boolean linkBankCoordinateToUser(String bankCoordinate, Authentication auth) throws EntityMissingException {
		return bankService.linkUserToBankCoordinate(auth.getName(), bankCoordinate);
	}
}
