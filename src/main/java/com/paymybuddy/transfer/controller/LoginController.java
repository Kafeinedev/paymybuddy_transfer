package com.paymybuddy.transfer.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	@GetMapping("/login")
	public ModelAndView login(Authentication auth) {
		if (auth != null && auth.isAuthenticated()) {
			return new ModelAndView("redirect:/mytransactions");
		}
		return new ModelAndView("/login");
	}
}
