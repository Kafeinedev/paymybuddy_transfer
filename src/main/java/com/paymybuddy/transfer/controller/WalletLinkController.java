package com.paymybuddy.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.service.WalletLinkService;

@RestController
public class WalletLinkController {

	@Autowired
	private WalletLinkService walletLinkService;

	@PostMapping("/walletlink")
	public WalletLink createWalletLink(String name, long senderId, long receiverId, Authentication auth)
			throws WrongUserException, InvalidArgumentException, EntityMissingException {
		return walletLinkService.createWalletLink(name, auth.getName(), senderId, receiverId);
	}

	@PatchMapping("/walletlink")
	public WalletLink updateWalletLinkName(long walletLinkId, String newName)
			throws EntityMissingException, InvalidArgumentException {
		return walletLinkService.updateName(walletLinkId, newName);
	}
}
