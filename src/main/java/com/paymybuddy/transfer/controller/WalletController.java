package com.paymybuddy.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.service.WalletService;

/**
 * Prototype wallet controller to allow wallet creation.
 */
@RestController
public class WalletController {

	@Autowired
	private WalletService walletService;

	/**
	 * Allow a connected user to add a wallet.
	 *
	 * @param currency the currency to be used by the wallet.
	 * @param auth     the current authentication token.
	 * @return the wallet newly created.
	 * @throws InvalidArgumentException in case the currency is invalid
	 * @throws EntityMissingException   in case the user does not exist.
	 */
	@PostMapping("/wallet")
	public Wallet createWallet(String currency, Authentication auth)
			throws InvalidArgumentException, EntityMissingException {
		return walletService.createWallet(auth.getName(), currency);
	}

}
