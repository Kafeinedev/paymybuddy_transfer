package com.paymybuddy.transfer.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.Fee;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.Wallet;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.repository.TransactionRepository;
import com.paymybuddy.transfer.repository.WalletLinkRepository;
import com.paymybuddy.transfer.repository.WalletRepository;

@Service
public class TransactionService {

	private Logger log = LogManager.getLogger("Transaction Service");

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private WalletLinkRepository walletLinkRepository;

	@Transactional
	public Transaction makeTransaction(long walletLinkId, BigDecimal amount, String description)
			throws EntityMissingException, InsufficientFundException {
		WalletLink link = findWalletLinkById(walletLinkId);
		Wallet sender = link.getSender();
		Wallet receiver = link.getReceiver();
		BigDecimal fee = feeCalculation(amount);
		BigDecimal totalCost = amount.add(fee).setScale(2, RoundingMode.HALF_UP);
		if (sender.getAmount().compareTo(totalCost) < 0) {
			log.error("Trying to make transaction without enough funds");
			throw new InsufficientFundException();
		}

		sender.setAmount(sender.getAmount().subtract(totalCost).setScale(2, RoundingMode.HALF_UP));
		receiver.setAmount(receiver.getAmount().add(amount).setScale(2, RoundingMode.HALF_UP));
		Transaction transaction = Transaction.builder().amount(amount.setScale(2, RoundingMode.HALF_UP)).link(link)
				.fee(fee).build();
		if (description != null) {
			transaction.setDescription(description);
		}

		walletRepository.save(sender);
		walletRepository.save(receiver);
		transactionRepository.save(transaction);

		return transaction;
	}

	public WalletLink findWalletLinkById(long walletLinkId) throws EntityMissingException {
		return walletLinkRepository.findById(walletLinkId).orElseThrow(() -> {
			log.error("Trying to make transaction with non existent WalletLink, id passed : " + walletLinkId);
			return new EntityMissingException();
		});
	}

	private BigDecimal feeCalculation(BigDecimal amount) {
		return amount.multiply(Fee.STANDARD_FEE).setScale(2, RoundingMode.HALF_UP);
	}
}
