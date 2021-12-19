package com.paymybuddy.transfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.transfer.model.User;

public interface IUserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByEmail(String email);
}