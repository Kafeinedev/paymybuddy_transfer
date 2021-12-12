package com.paymybuddy.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.transfer.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}