package com.paymybuddy.transfer.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.WalletLinkRepository;
import com.paymybuddy.transfer.repository.WalletRepository;
import com.paymybuddy.transfer.service.WalletLinkService;

/**
 * WalletLinkServiceImpl.
 */
@Service
public class WalletLinkServiceImpl implements WalletLinkService {

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private WalletLinkRepository walletLinkRepository;

	private Logger log = LogManager.getLogger("WalletLink Service");

	@Override
	@Transactional
	public List<WalletLink> getAllOutgoingLinksByUserEmail(String email) {
		return walletLinkRepository.findBySenderOwnerEmail(email);
	}

	@Override
	@Transactional
	public WalletLink createWalletLink(String name, String emailUserSender, long idSender, long idReceiver)
			throws WrongUserException, InvalidArgumentException, EntityMissingException {
		validateName(name);

		Wallet sender = walletRepository.findById(idSender).orElseThrow(() -> {
			log.error("Could not find sender wallet with id : " + idSender);
			return new EntityMissingException();
		});
		Wallet receiver = walletRepository.findById(idReceiver).orElseThrow(() -> {
			log.error("Could not find receiver wallet with id : " + idReceiver);
			return new EntityMissingException();
		});

		WalletLink walletLink = WalletLink.builder().name(name).sender(sender).receiver(receiver).build();

		validateWalletLink(walletLink, emailUserSender, sender.getOwner().getEmail());

		return walletLinkRepository.save(walletLink);
	}

	private boolean validateName(String name) throws InvalidArgumentException {
		if (name.length() > 16) {
			log.error("Could not create or update walletLink name too long");
			throw new InvalidArgumentException();
		}
		return true;
	}

	private boolean validateWalletLink(WalletLink walletLink, String emailUserSender, String emailOwnerSenderWallet)
			throws WrongUserException, InvalidArgumentException {
		if (!emailOwnerSenderWallet.equals(emailUserSender)) {
			log.error("User with email : " + emailUserSender
					+ "tried to create a walletLinkWithout owning sending wallet");
			throw new WrongUserException();
		}
		if (!walletLink.getSender().getCurrency().equals(walletLink.getReceiver().getCurrency())) {
			log.error("User with email : " + emailUserSender
					+ "tried to create a walletLink between two wallets of differents currencies");
			throw new InvalidArgumentException();
		}
		return true;
	}

	@Override
	@Transactional
	public WalletLink updateWalletLinkName(long linkId, String newName)
			throws EntityMissingException, InvalidArgumentException {
		validateName(newName);

		WalletLink link = walletLinkRepository.findById(linkId).orElseThrow(() -> {
			log.error("Could not find walletLink with id : " + linkId);
			return new EntityMissingException();
		});

		link.setName(newName);

		return walletLinkRepository.save(link);
	}

}
