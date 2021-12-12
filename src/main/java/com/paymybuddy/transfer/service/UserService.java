package com.paymybuddy.transfer.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.TransactionRepository;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.repository.WalletLinkRepository;

@Service
public class UserService {

	@Autowired
	private WalletLinkRepository walletLinkRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserRepository userRepository;

	public List<WalletLink> getAllOutgoingLinksByUser(User u) {
		return walletLinkRepository.findOutgoingLinksByUserEmail(u.getEmail());
	}

	public List<Transaction> getAllTransactionsByUser(User u) {
		return transactionRepository.findAllByUserEmail(u.getEmail());
	}

	public List<Triplet<String, String, BigDecimal>> getTransactionsInfoByUserAndPage(User u) {
		List<Triplet<String, String, BigDecimal>> transactionsInfo = new ArrayList<Triplet<String, String, BigDecimal>>();
		List<Transaction> transactions = transactionRepository.findAllByUserEmail(u.getEmail());

		for (Transaction transaction : transactions) {
			transactionsInfo.add(new Triplet<String, String, BigDecimal>(
					userRepository.findByTransactionIdAndOtherPartyEmail(transaction.getId(), u.getEmail()).getName(),
					transaction.getDescription(), transaction.getAmount()));
		}

		return transactionsInfo;
	}
}
