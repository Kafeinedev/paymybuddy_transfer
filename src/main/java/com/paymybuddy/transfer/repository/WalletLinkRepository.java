package com.paymybuddy.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.WalletLink;

@Repository
public interface WalletLinkRepository extends JpaRepository<WalletLink, Long> {

}
