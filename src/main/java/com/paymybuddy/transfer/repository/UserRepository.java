package com.paymybuddy.transfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.transfer.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT user FROM User user WHERE "
			+ "(user.id=(SELECT transaction.link.sender.owner.id FROM Transaction transaction WHERE transaction.id=?1)"
			+ "OR user.id=(SELECT transaction.link.receiver.owner.id FROM Transaction transaction WHERE transaction.id=?1)) "
			+ "AND (user.email!=?2)")
	public Optional<User> findByTransactionIdAndOtherPartyEmail(long id, String email);

}