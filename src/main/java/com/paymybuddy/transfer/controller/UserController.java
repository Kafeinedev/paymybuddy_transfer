package com.paymybuddy.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.service.IUserService;

@RestController
public class UserController {

	@Autowired
	private IUserService userService;

	@PostMapping("/createuser")
	public User createUser(String name, String email, String password) throws InvalidArgumentException {
		return userService.createUser(name, email, password);
	}

	@PutMapping("/user")
	public User updateUser(String name, String email, String password, Authentication auth)
			throws EntityMissingException, InvalidArgumentException {
		return userService.updateUser(auth.getName(), name, email, password);
	}
}
