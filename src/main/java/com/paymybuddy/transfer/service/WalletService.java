package com.paymybuddy.transfer.service;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.Currencies;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.repository.WalletRepository;

/**
 * WalletServiceImpl.
 */
@Service
public class WalletService implements IWalletService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WalletRepository walletRepository;

	private Logger log = LogManager.getLogger("Wallet Service");

	@Override
	@Transactional
	public Wallet createWallet(String ownerEmail, String currency)
			throws InvalidArgumentException, EntityMissingException {
		validateCurrency(currency);
		User owner = userRepository.findByEmail(ownerEmail).orElseThrow(() -> {
			log.error("Could not find user with email : " + ownerEmail);
			return new EntityMissingException();
		});

		Wallet wallet = Wallet.builder().owner(owner).currency(currency).build();
		return walletRepository.save(wallet);
	}

	private boolean validateCurrency(String currency) throws InvalidArgumentException {
		if (!Currencies.AUTHORIZED_CURRENCIES.contains(currency)) {
			log.error("Could not create wallet not an authorised currency");
			throw new InvalidArgumentException();
		}
		return true;
	}

}
