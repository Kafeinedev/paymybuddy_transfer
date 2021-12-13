package com.paymybuddy.transfer.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.PageSize;
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
		return new ArrayList<>();// transactionRepository.findAllByUserEmail(u.getEmail());
	}

	public Page<Triplet<String, String, BigDecimal>> getTransactionsInfoByUserAndPage(User u, int page) {
		List<Triplet<String, String, BigDecimal>> transactionsInfo = new ArrayList<Triplet<String, String, BigDecimal>>();
		Pageable pageRequest = PageRequest.of(page, PageSize.TRANSACTIONS_INFO);

		Page<Transaction> transactions = transactionRepository.findAllByUserEmail(u.getEmail(), pageRequest);

		for (Transaction transaction : transactions) {
			transactionsInfo.add(new Triplet<String, String, BigDecimal>(userRepository
					.findByTransactionIdAndOtherPartyEmail(transaction.getId(), u.getEmail()).orElseThrow().getName(),
					transaction.getDescription(), transaction.getAmount()));
		}

		return new PageImpl<>(transactionsInfo, pageRequest, transactions.getTotalElements());
	}
}
