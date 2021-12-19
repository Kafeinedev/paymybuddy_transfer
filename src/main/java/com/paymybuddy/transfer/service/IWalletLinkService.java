package com.paymybuddy.transfer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.WalletLink;

@Service
public interface IWalletLinkService {

	List<WalletLink> getAllOutgoingLinksByUserEmail(String email);

	WalletLink createWalletLink(String name, String emailUserSender, long idSender, long idReceiver)
			throws WrongUserException, InvalidArgumentException, EntityMissingException;

	WalletLink updateWalletLinkName(long linkId, String newName) throws EntityMissingException, InvalidArgumentException;

}