package com.paymybuddy.transfer.service.impl;

import java.util.List;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.constant.RegexStringPattern;
import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.User;
import com.paymybuddy.transfer.repository.UserRepository;
import com.paymybuddy.transfer.service.UserService;

/**
 * UserServiceImpl.
 */
@Service
public class DefaultUserService implements UserDetailsService, UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder encoder;

	private Logger log = LogManager.getLogger("User Service");

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> {
			log.error("Trying to load inexistent user with email : " + email);
			return new UsernameNotFoundException("Could not find user with email : " + email);
		});

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Override
	@Transactional
	public User createUser(String name, String email, String password) throws InvalidArgumentException {
		validateName(name);
		validateEmail(email);
		validatePassword(password);
		if (userRepository.findByEmail(email).isPresent()) {
			log.error("could not create user email already present");
			throw new InvalidArgumentException();
		}

		User user = User.builder().email(email).name(name).password(encoder.encode(password)).build();
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User updateUser(String userEmail, String name, String email, String password)
			throws EntityMissingException, InvalidArgumentException {
		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
			log.error("Could not update user email not found");
			return new EntityMissingException();
		});
		if (name != null) {
			validateName(name);
			user.setName(name);
		}
		if (email != null) {
			validateEmail(email);
			if (userRepository.findByEmail(email).isPresent()) {
				log.error("Could not update user email : " + email + "already exist");
				throw new InvalidArgumentException();
			}
			user.setEmail(email);
		}
		if (password != null) {
			validatePassword(password);
			user.setPassword(encoder.encode(password));
		}
		return userRepository.save(user);
	}

	private boolean validateEmail(String email) throws InvalidArgumentException {
		if (!Pattern.matches(RegexStringPattern.EMAIL, email)) {
			log.error("Could not create or update user email invalid");
			throw new InvalidArgumentException();
		}
		return true;
	}

	private boolean validatePassword(String password) throws InvalidArgumentException {
		if (!Pattern.matches(RegexStringPattern.PASSWORD, password)) {
			log.error("Could not create or update user password invalid");
			throw new InvalidArgumentException();
		}
		return true;
	}

	private boolean validateName(String name) throws InvalidArgumentException {
		if (name.length() > 16) {
			log.error("Could not create or update user name too long");
			throw new InvalidArgumentException();
		}
		return true;
	}
}
