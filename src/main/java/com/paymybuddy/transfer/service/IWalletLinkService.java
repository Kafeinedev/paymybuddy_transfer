package com.paymybuddy.transfer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.WalletLink;

/**
 * Interface for WalletLinkService.
 */
@Service
public interface IWalletLinkService {

	/**
	 * Gets the all links going outward by a user via email.
	 *
	 * @param email the email of the user owning the links.
	 * @return all outgoing links owned by the user
	 */
	List<WalletLink> getAllOutgoingLinksByUserEmail(String email);

	/**
	 * Creates wallet link.
	 *
	 * @param name            name of the walletlink
	 * @param emailUserSender email of the connected user trying to make an
	 *                        outgoinglink
	 * @param idSender        the id of the sender wallet
	 * @param idReceiver      the id of the receiver wallet
	 * @return the created wallet link
	 * @throws WrongUserException       in case the connected user is not the one
	 *                                  owning the sending wallet.
	 * @throws InvalidArgumentException in case an argument is invalid.
	 * @throws EntityMissingException   in case an entity is missing.
	 */
	WalletLink createWalletLink(String name, String emailUserSender, long idSender, long idReceiver)
			throws WrongUserException, InvalidArgumentException, EntityMissingException;

	/**
	 * Update wallet link name.
	 *
	 * @param linkId  the link id of the walletlink to update
	 * @param newName the new name
	 * @return the updated wallet link
	 * @throws EntityMissingException   in case the walletlink doesnt exist
	 * @throws InvalidArgumentException in case an argument is invalid
	 */
	WalletLink updateWalletLinkName(long linkId, String newName)
			throws EntityMissingException, InvalidArgumentException;

}