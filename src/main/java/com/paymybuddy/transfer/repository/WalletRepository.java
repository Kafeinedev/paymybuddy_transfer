package com.paymybuddy.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
