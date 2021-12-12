package com.paymybuddy.transfer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.WalletLink;

@Repository
public interface WalletLinkRepository extends JpaRepository<WalletLink, Long> {

	@Query("SELECT walletLinks FROM WalletLink walletLinks JOIN walletLinks.sender sender JOIN sender.owner owner WHERE owner.email=?1")
	public List<WalletLink> findOutgoingLinksByUserEmail(String email);
}
