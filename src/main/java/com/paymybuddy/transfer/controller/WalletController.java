package com.paymybuddy.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.service.IWalletService;

@RestController
public class WalletController {

	@Autowired
	private IWalletService walletService;

	@PostMapping("/wallet")
	public Wallet createWallet(String currency, Authentication auth)
			throws InvalidArgumentException, EntityMissingException {
		return walletService.createWallet(auth.getName(), currency);
	}

}
