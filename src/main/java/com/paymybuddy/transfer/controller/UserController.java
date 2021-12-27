package com.paymybuddy.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.service.UserService;

/**
 * Prototype user controller to allow user creation and modification.
 */
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * Allow creation of a new user.
	 *
	 * @param name     the name to be used by the new user.
	 * @param email    the email to be used by the new user.
	 * @param password the password to be used by the new user.
	 * @return the newly created user.
	 * @throws InvalidArgumentException in case parameters are incorrects or email
	 *                                  already in use.
	 */
	@PostMapping("/createuser")
	public User createUser(String name, String email, String password) throws InvalidArgumentException {
		return userService.createUser(name, email, password);
	}

	/**
	 * Update user.
	 *
	 * @param name     the new name can be null.
	 * @param email    the new email can be null.
	 * @param password the new password can be null.
	 * @param auth     the current authentication token
	 * @return the user
	 * @throws EntityMissingException   in case user does not exist.
	 * @throws InvalidArgumentException in case a parameter is invalid.
	 */
	@PutMapping("/user")
	public User updateUser(String name, String email, String password, Authentication auth)
			throws EntityMissingException, InvalidArgumentException {
		return userService.updateUser(auth.getName(), name, email, password);
	}
}
