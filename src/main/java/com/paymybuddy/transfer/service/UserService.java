package com.paymybuddy.transfer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.Currencies;
import com.paymybuddy.transfer.constant.PageSize;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.TransactionRepository;
import com.paymybuddy.transfer.repository.WalletLinkRepository;

@Service
public class UserService {

	@Autowired
	private WalletLinkRepository walletLinkRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public List<WalletLink> getAllOutgoingLinksByUserEmail(String email) {
		return walletLinkRepository.findBySenderOwnerEmail(email);
	}

	public Page<String[]> getTransactionsInfoByUserEmailAndPage(String email, int page) {
		List<String[]> transactionsInfo = new ArrayList<String[]>();
		Pageable pageRequest = PageRequest.of(page, PageSize.TRANSACTIONS_INFO);

		Page<Transaction> transactions = transactionRepository.findByLinkSenderOwnerEmailOrderByDateDesc(email,
				pageRequest);

		for (Transaction transaction : transactions) {
			WalletLink link = walletLinkRepository.findByTransactions(transaction).orElseThrow();
			Wallet sender = link.getSender();
			String[] infos = { link.getName(), transaction.getDescription(),
					transaction.getAmount().toString() + Currencies.SYMBOLS.get(sender.getCurrency()) };
			transactionsInfo.add(infos);
		}

		return new PageImpl<String[]>(transactionsInfo, pageRequest, transactions.getTotalElements());
	}
}
