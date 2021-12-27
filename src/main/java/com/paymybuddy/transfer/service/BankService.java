package com.paymybuddy.transfer.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.BankCoordinate;
import com.paymybuddy.transfer.model.BankTransaction;

/**
 * Interface for BankService.
 */
@Service
public interface BankService {

	/**
	 * Link user to a bank coordinate.
	 *
	 * @param userEmail     the user email
	 * @param accountNumber the account number in the bank coordinate
	 * @return true, if successful
	 * @throws EntityMissingException in case an entity is missing.
	 */
	boolean linkUserToBankCoordinate(String userEmail, String accountNumber) throws EntityMissingException;

	/**
	 * Creates a bank coordinate.
	 *
	 * @param accountNumber the account number of the coordinate
	 * @return the created bank coordinate
	 * @throws InvalidArgumentException in case the account number is invalid
	 */
	BankCoordinate createBankCoordinate(String accountNumber) throws InvalidArgumentException;

	/**
	 * Execute a withdrawal from a wallet to a physical bank account.
	 *
	 * @param userEmail        the email of the user trying to execute the
	 *                         withdrawal
	 * @param bankCoordinateId the id of the target bank coordinate
	 * @param walletId         the id of the wallet sending the funds.
	 * @param amount           the amount of fund to be withdraw.
	 * @return the executed bank transaction
	 * @throws EntityMissingException    in case an entity is missing.
	 * @throws InsufficientFundException in case wallet does not hold enough fund.
	 * @throws WrongUserException        in case the current connected user is not
	 *                                   the one owning the wallet.
	 * @throws InvalidArgumentException  in case one argument is invalid.
	 */
	BankTransaction withdraw(String userEmail, long bankCoordinateId, long walletId, BigDecimal amount)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException;

	/**
	 * Execute a funding toward a wallet from a physical bank account.
	 *
	 * @param userEmail        the email of the user trying to execute the funding
	 * @param bankCoordinateId the id of the originating bank coordinate
	 * @param walletId         the id of the wallet receiving the funds.
	 * @param amount           the amount of fund to be withdraw.
	 * @return the executed bank transaction
	 * @throws EntityMissingException   in case an entity is missing.
	 * @throws WrongUserException       in case the current connected user is not
	 *                                  the one owning the wallet.
	 * @throws InvalidArgumentException in case one argument is invalid.
	 */
	BankTransaction fund(String userEmail, long bankCoordinateId, long walletId, BigDecimal amount)
			throws EntityMissingException, WrongUserException, InvalidArgumentException;

}