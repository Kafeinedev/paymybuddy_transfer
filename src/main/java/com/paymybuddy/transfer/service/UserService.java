package com.paymybuddy.transfer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.WalletLinkRepository;

@Service
public class UserService {

	@Autowired
	private WalletLinkRepository walletLinkRepository;

	public List<WalletLink> getAllOutgoingLinksByUser(User u) {
		return walletLinkRepository.findOutgoingLinksByUserEmail(u.getEmail());
	}
}
