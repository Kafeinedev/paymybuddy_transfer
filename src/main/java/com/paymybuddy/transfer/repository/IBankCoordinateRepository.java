package com.paymybuddy.transfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.BankCoordinate;

@Repository
public interface IBankCoordinateRepository extends JpaRepository<BankCoordinate, Long> {

	Optional<BankCoordinate> findByAccountNumber(String string);

}
