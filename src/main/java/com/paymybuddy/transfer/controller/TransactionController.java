package com.paymybuddy.transfer.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
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
	public ModelAndView transactionList() {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("connections", userService.getAllOutgoingLinksByUser(userRep.findById(1L).orElseThrow()));

		return new ModelAndView("transactions", model);
	}

	@PostMapping("/transaction")
	public ModelAndView executeTransaction(long connection, BigDecimal amount)
			throws EntityMissingException, InsufficientFundException {
		transactionService.makeTransaction(connection, amount, null);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("connections", userService.getAllOutgoingLinksByUser(userRep.findById(1L).orElseThrow()));

		return new ModelAndView(new RedirectView("/transactions"), model);
	}

}
