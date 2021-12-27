package com.paymybuddy.transfer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.Transaction;
import com.paymybuddy.transfer.model.WalletLink;

/**
 * DAL forWalletLinks.
 */
@Repository
public interface WalletLinkRepository extends JpaRepository<WalletLink, Long> {

	/**
	 * Find by walletlinks via the email of the sending user.
	 *
	 * @param email the email of the user
	 * @return the list of walletlinks the user own.
	 */
	public List<WalletLink> findBySenderOwnerEmail(String email);

	/**
	 * Find by transactions.
	 *
	 * @param transaction the transaction
	 * @return the optional of the walletLink
	 */
	public Optional<WalletLink> findByTransactions(Transaction transaction);
}
