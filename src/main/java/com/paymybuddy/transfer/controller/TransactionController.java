package com.paymybuddy.transfer.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InsufficientFundException;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.service.TransactionService;
import com.paymybuddy.transfer.service.UserService;

@Controller
public class TransactionController {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private UserRepository userRep;

	@GetMapping("/transactions")
	public ModelAndView transactionList(@RequestParam Optional<Integer> page) {
		Map<String, Object> model = new HashMap<String, Object>();
		int currentPage = page.orElse(1) - 1;// List start at 0
		Page<Triplet<String, String, BigDecimal>> transactionsInfoPage = userService
				.getTransactionsInfoByUserAndPage(userRep.findById(1L).orElseThrow(), currentPage);

		model.put("connections", userService.getAllOutgoingLinksByUser(userRep.findById(1L).orElseThrow()));
		model.put("currentPage", currentPage + 1);// View consider start at 1
		model.put("totalPages", transactionsInfoPage.getTotalPages());
		model.put("totalItems", transactionsInfoPage.getTotalElements());
		model.put("transactions", transactionsInfoPage.getContent());

		return new ModelAndView("transactions", model);
	}

	@PostMapping("/transaction")
	public View executeTransaction(long connection, BigDecimal amount)
			throws EntityMissingException, InsufficientFundException {
		transactionService.makeTransaction(connection, amount, null);

		return new RedirectView("/transactions");
	}

}
