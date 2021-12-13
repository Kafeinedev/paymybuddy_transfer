package com.paymybuddy.transfer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	public Page<Transaction> findByLinkSenderOwnerEmailOrderByDateDesc(String email, Pageable page);
}
