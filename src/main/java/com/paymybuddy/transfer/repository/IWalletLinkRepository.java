package com.paymybuddy.transfer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.WalletLink;

@Repository
public interface IWalletLinkRepository extends JpaRepository<WalletLink, Long> {

	public List<WalletLink> findBySenderOwnerEmail(String email);

	public Optional<WalletLink> findByTransactions(Transaction transaction);
}
