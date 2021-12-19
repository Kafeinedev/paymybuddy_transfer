package com.paymybuddy.transfer.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;

@Service
public interface IUserService {

	User createUser(String name, String email, String password) throws InvalidArgumentException;

	User updateUser(String userEmail, String name, String email, String password)
			throws EntityMissingException, InvalidArgumentException;

}