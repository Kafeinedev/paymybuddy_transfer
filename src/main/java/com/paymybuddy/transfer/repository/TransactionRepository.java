package com.paymybuddy.transfer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.Transaction;

/**
 * DAL for transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	/**
	 * Find the transactions via the email of the user who sent the transaction.
	 *
	 * @param email of the user who sent the transaction
	 * @param page  the pageable containing the elements to create the page
	 * @return the page containing the transaction
	 */
	public Page<Transaction> findByLinkSenderOwnerEmailOrderByDateDesc(String email, Pageable page);
}
