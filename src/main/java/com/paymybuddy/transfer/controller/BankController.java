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
import com.paymybuddy.transfer.service.BankService;

/*
 * Prototype bank controller to allow user for funding and withdrawal.
 */
@RestController
public class BankController {

	@Autowired
	private BankService bankService;

	/**
	 * This method allow for an user to fund a wallet he owns from a bankCoordinate
	 * he is linked to.
	 *
	 * @param bankCoordinateId the bank coordinate id
	 * @param walletId         the wallet id
	 * @param amount           the amount
	 * @param auth             the current authentication token
	 * @return the bank transaction made by the application
	 * 
	 * @throws EntityMissingException   in case the wallet or the bankCoordinate is
	 *                                  not found
	 * @throws WrongUserException       in case the logged in user try to access a
	 *                                  ressource he doesnt own.
	 * @throws InvalidArgumentException if arguments given by the users are not
	 *                                  corrects.
	 */
	@PostMapping("/fund")
	public BankTransaction fund(long bankCoordinateId, long walletId, BigDecimal amount, Authentication auth)
			throws EntityMissingException, WrongUserException, InvalidArgumentException {
		return bankService.fund(auth.getName(), bankCoordinateId, walletId, amount);
	}

	/**
	 * This method allow for an user to withdraw from a wallet he owns toward a
	 * bankCoordinate he is linked to.
	 *
	 * @param bankCoordinateId the bank coordinate id
	 * @param walletId         the wallet id
	 * @param amount           the amount
	 * @param auth             the auth the current authentication token
	 * @return the bank transaction made by the application
	 * 
	 * @throws EntityMissingException    in case the wallet or the bankCoordinate is
	 *                                   not found
	 * @throws WrongUserException        in case the logged in user try to access a
	 *                                   ressource he doesnt own.
	 * @throws InvalidArgumentException  if arguments given by the users are not
	 *                                   corrects.
	 * @throws InsufficientFundException in case wallet does not hold enough funds.
	 */
	@PostMapping("/withdraw")
	public BankTransaction withdraw(long bankCoordinateId, long walletId, BigDecimal amount, Authentication auth)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException {
		return bankService.withdraw(auth.getName(), bankCoordinateId, walletId, amount);
	}

	/**
	 * Create a bank coordinate. This method does not link the bankcoordinate to an
	 * user.
	 *
	 * @param bankCoordinate the bank account to be added
	 * @return the bank coordinate added
	 * @throws InvalidArgumentException in case the bank account is invalid.
	 */
	@PostMapping("/bankcoordinate")
	public BankCoordinate createBankCoordinate(String bankCoordinate) throws InvalidArgumentException {
		return bankService.createBankCoordinate(bankCoordinate);
	}

	/**
	 * Link bank coordinate to an user.
	 *
	 * @param bankCoordinate the bank coordinate
	 * @param auth           the current authentication token
	 * @return true, if successful
	 * @throws EntityMissingException in case the bank coordinate is not found.
	 */
	@PostMapping("/linkbankcoordinatetouser")
	public boolean linkBankCoordinateToUser(String bankCoordinate, Authentication auth) throws EntityMissingException {
		return bankService.linkUserToBankCoordinate(auth.getName(), bankCoordinate);
	}
}
