package com.paymybuddy.transfer.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.Wallet;

/**
 * Interface for WalletService.
 */
@Service
public interface IWalletService {

	/**
	 * Creates the wallet.
	 *
	 * @param ownerEmail current connected user that will own the wallet.
	 * @param currency   the currency of the wallet to be created.
	 * @return the created wallet
	 * @throws InvalidArgumentException in case the currency is invalid.
	 * @throws EntityMissingException   in case the user is not found.
	 */
	Wallet createWallet(String ownerEmail, String currency) throws InvalidArgumentException, EntityMissingException;

}