package com.paymybuddy.transfer.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.service.TransactionService;
import com.paymybuddy.transfer.service.WalletLinkService;

/**
 * The Controller handling transactions.
 */
@Controller
public class TransactionController {

	@Autowired
	private WalletLinkService walletLinkService;

	@Autowired
	private TransactionService transactionService;

	/**
	 * Display my transactions.
	 *
	 * @param page the page number of user transactions to be displayed if empty
	 *             default to zero.
	 * @param auth current authentication token.
	 * @return the model and view of "/mytransactions".
	 * @throws InvalidArgumentException if a parameter is invalid.
	 */
	@GetMapping("/mytransactions")
	public ModelAndView myTransactions(@RequestParam Optional<Integer> page, Authentication auth)
			throws InvalidArgumentException {
		Map<String, Object> model = new HashMap<String, Object>();

		int currentPage = page.orElse(1) - 1;// first page == 0
		List<WalletLink> connections = walletLinkService.getAllOutgoingLinksByUserEmail(auth.getName());
		Page<String[]> transactionsInfoPage = transactionService.getTransactionsInfoByUserEmailAndPage(auth.getName(),
				currentPage);

		model.put("connections", connections);
		model.put("currentPage", currentPage + 1);// View consider first page == 1
		model.put("totalPages", transactionsInfoPage.getTotalPages());
		model.put("totalItems", transactionsInfoPage.getTotalElements());
		model.put("transactions", transactionsInfoPage.getContent());

		return new ModelAndView("transactions", model);
	}

	/**
	 * Allow the user to make a transaction where he is the sender.
	 *
	 * @param connection The walletLinkId to be used for the transaction.
	 * @param amount     the amount to be used for the transaction.
	 * @param auth       the current authentication token.
	 * @return redirect toward "/mytransactions"
	 * @throws EntityMissingException    in case the walletLink is not found.
	 * @throws InsufficientFundException if wallet does not hold enough fund.
	 * @throws WrongUserException        if the connected user tries to use a
	 *                                   walletLink he does not own.
	 * @throws InvalidArgumentException  if an argument is wrong.
	 */
	@PostMapping("/transaction")
	public View executeTransaction(long connection, BigDecimal amount, Authentication auth)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException {
		transactionService.makeTransaction(auth.getName(), connection, amount, null);

		return new RedirectView("/mytransactions");
	}

	/**
	 * Update the description of an existing transaction.
	 *
	 * @param transactionId  the id of the transaction to update
	 * @param newDescription the new description
	 * @return the updated transaction
	 * @throws EntityMissingException   in case the transaction does not exist.
	 * @throws InvalidArgumentException in case the new description is invalid.
	 */
	@PatchMapping("/transaction")
	@ResponseBody
	public Transaction updateTransactionDescription(long transactionId, String newDescription)
			throws EntityMissingException, InvalidArgumentException {
		return transactionService.updateTransactionDescription(transactionId, newDescription);
	}
}
