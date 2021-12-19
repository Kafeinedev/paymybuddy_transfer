package com.paymybuddy.transfer.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;

/**
 * Interface for UserService.
 */
@Service
public interface IUserService {

	/**
	 * Create a new user.
	 *
	 * @param name     the name to be used by the new user.
	 * @param email    the email to be used by the new user.
	 * @param password the password to be used by the new user.
	 * @return the newly created user.
	 * @throws InvalidArgumentException in case parameters are incorrects or email
	 *                                  already in use.
	 */
	User createUser(String name, String email, String password) throws InvalidArgumentException;

	/**
	 * Update user.
	 *
	 * @param name     the new name, can be null.
	 * @param email    the new email, can be null.
	 * @param password the new password, can be null.
	 * @return the updated user
	 * @throws EntityMissingException   in case user does not exist.
	 * @throws InvalidArgumentException in case a parameter is invalid.
	 */
	User updateUser(String userEmail, String name, String email, String password)
			throws EntityMissingException, InvalidArgumentException;

}