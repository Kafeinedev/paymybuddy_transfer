package com.paymybuddy.transfer.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Transaction;

/**
 * Interface for TransactionService.
 */
@Service
public interface TransactionService {

	/**
	 * Make transaction.
	 *
	 * @param emitterEmail the email of the connected user emitting the transaction
	 * @param walletLinkId the wallet link id to be used in the transaction
	 * @param amount       the amount of the transaction to be made
	 * @param description  the description of the transaction to be made can be null
	 * @return the created transaction
	 * @throws EntityMissingException    in case an entity could not be found.
	 * @throws InsufficientFundException in case the sending wallet does not hold
	 *                                   enough funds.
	 * @throws WrongUserException        in case a user tries to send a transaction
	 *                                   from a wallet he doesnt own.
	 * @throws InvalidArgumentException  in case one or multiples arguments are
	 *                                   invalid.
	 */
	Transaction makeTransaction(String emitterEmail, long walletLinkId, BigDecimal amount, String description)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException;

	/**
	 * Update transaction description.
	 *
	 * @param id          the id of the transaction to be updated
	 * @param description the new description
	 * @return the updated transaction
	 * @throws EntityMissingException   in case transaction is not found
	 * @throws InvalidArgumentException in case the new description is invalid
	 */
	Transaction updateTransactionDescription(long id, String description)
			throws EntityMissingException, InvalidArgumentException;

	/**
	 * Gets the transactions info linked to an user in a page.
	 *
	 * @param email the email of the user requesting the page.
	 * @param page  the page number to be displayed.
	 * @return a page containing the requested transactions info (walletlinkname,
	 *         description, amount+currency symbol).
	 * @throws InvalidArgumentException in case an argument is invalid
	 */
	Page<String[]> getTransactionsInfoByUserEmailAndPage(String email, int page) throws InvalidArgumentException;

}