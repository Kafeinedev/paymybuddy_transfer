package com.paymybuddy.transfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.transfer.model.User;

/**
 * DAL for user.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Find by email.
	 *
	 * @param email the email of the user
	 * @return the optional of the user
	 */
	public Optional<User> findByEmail(String email);
}