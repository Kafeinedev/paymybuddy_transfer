package com.paymybuddy.transfer.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller handling the custom login page.
 */
@Controller
public class LoginController {

	/**
	 * Display login page.
	 *
	 * @param auth current authentication token
	 * @return the login page if not logged in, if logged in redirect toward
	 *         "/mytransactions".
	 */
	@GetMapping("/login")
	public ModelAndView login(Authentication auth) {
		if (auth != null && auth.isAuthenticated()) {
			return new ModelAndView("redirect:/mytransactions");
		}
		return new ModelAndView("/login");
	}
}
