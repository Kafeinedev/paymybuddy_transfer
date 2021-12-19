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
import com.paymybuddy.transfer.service.IWalletLinkService;

/**
 * Prototype walletLink controller to allow walletLink creation and name
 * modification.
 */
@RestController
public class WalletLinkController {

	@Autowired
	private IWalletLinkService walletLinkService;

	/**
	 * Creates the wallet link.
	 *
	 * @param name       the name of the wallet link to create.
	 * @param senderId   the id of the sender wallet. this wallet must be own by the
	 *                   current connected user.
	 * @param receiverId the id of the receiver waller.
	 * @param auth       the current authentication token.
	 * @return the created wallet link
	 * @throws WrongUserException       in case the user tries to create a
	 *                                  connection by using a sending wallet he does
	 *                                  not own
	 * @throws InvalidArgumentException in case a parameter is invalid.
	 * @throws EntityMissingException   in case concerned entity can not be found.
	 */
	@PostMapping("/walletlink")
	public WalletLink createWalletLink(String name, long senderId, long receiverId, Authentication auth)
			throws WrongUserException, InvalidArgumentException, EntityMissingException {
		return walletLinkService.createWalletLink(name, auth.getName(), senderId, receiverId);
	}

	/**
	 * Update the name of an existing walletLink.
	 *
	 * @param walletLinkId the wallet link id
	 * @param newName      the new name
	 * @return the updated wallet link
	 * @throws EntityMissingException   in case the walletLink does not exist.
	 * @throws InvalidArgumentException in case the new name is invalid.
	 */
	@PatchMapping("/walletlink")
	public WalletLink updateWalletLinkName(long walletLinkId, String newName)
			throws EntityMissingException, InvalidArgumentException {
		return walletLinkService.updateWalletLinkName(walletLinkId, newName);
	}
}
