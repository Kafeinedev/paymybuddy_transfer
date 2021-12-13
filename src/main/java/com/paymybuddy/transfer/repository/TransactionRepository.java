package com.paymybuddy.transfer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("SELECT transactions FROM Transaction transactions JOIN transactions.link link "
			+ "WHERE (link.sender.owner.email=?1 OR link.receiver.owner.email=?1) ORDER BY date DESC")
	public Page<Transaction> findAllByUserEmail(String email, Pageable page);
}
