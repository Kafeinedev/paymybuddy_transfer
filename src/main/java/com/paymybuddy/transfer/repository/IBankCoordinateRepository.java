package com.paymybuddy.transfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.transfer.model.BankCoordinate;

/**
 * DAL for bankCoordinate
 */
@Repository
public interface IBankCoordinateRepository extends JpaRepository<BankCoordinate, Long> {

	/**
	 * Find by account number.
	 *
	 * @param accountNumber the account number to find.
	 * @return the optional containing the bankcoordinate.
	 */
	Optional<BankCoordinate> findByAccountNumber(String accountNumber);

}
