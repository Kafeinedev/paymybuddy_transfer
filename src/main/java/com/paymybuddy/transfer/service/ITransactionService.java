package com.paymybuddy.transfer.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Transaction;

@Service
public interface ITransactionService {

	Transaction makeTransaction(String emitterEmail, long walletLinkId, BigDecimal amount, String description)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException;

	Transaction updateTransactionDescription(long id, String description)
			throws EntityMissingException, InvalidArgumentException;

	Page<String[]> getTransactionsInfoByUserEmailAndPage(String email, int page) throws InvalidArgumentException;

}