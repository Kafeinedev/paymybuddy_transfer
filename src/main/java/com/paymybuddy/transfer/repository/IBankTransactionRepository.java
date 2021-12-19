package com.paymybuddy.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.BankTransaction;

/**
 * DAL for bankTransaction.
 */
@Repository
public interface IBankTransactionRepository extends JpaRepository<BankTransaction, Long> {

}
