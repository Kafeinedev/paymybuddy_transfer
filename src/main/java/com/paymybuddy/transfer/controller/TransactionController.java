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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.exception.WrongUserException;
import com.paymybuddy.transfer.model.WalletLink;
import com.paymybuddy.transfer.service.TransactionService;
import com.paymybuddy.transfer.service.UserService;

@Controller
public class TransactionController {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/mytransactions")
	public ModelAndView myTransactions(@RequestParam Optional<Integer> page, Authentication auth)
			throws InvalidArgumentException {
		Map<String, Object> model = new HashMap<String, Object>();

		int currentPage = page.orElse(1) - 1;// first page == 0
		List<WalletLink> connections = userService.getAllOutgoingLinksByUserEmail(auth.getName());
		Page<String[]> transactionsInfoPage = userService.getTransactionsInfoByUserEmailAndPage(auth.getName(),
				currentPage);

		model.put("connections", connections);
		model.put("currentPage", currentPage + 1);// View consider first page == 1
		model.put("totalPages", transactionsInfoPage.getTotalPages());
		model.put("totalItems", transactionsInfoPage.getTotalElements());
		model.put("transactions", transactionsInfoPage.getContent());

		return new ModelAndView("transactions", model);
	}

	@PostMapping("/transaction")
	public View executeTransaction(long connection, BigDecimal amount, Authentication auth)
			throws EntityMissingException, InsufficientFundException, WrongUserException, InvalidArgumentException {
		transactionService.makeTransaction(auth.getName(), connection, amount, null);

		return new RedirectView("/mytransactions");
	}

}
