package com.paymybuddy.transfer.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "bank_coordinates")
@NoArgsConstructor
@AllArgsConstructor
public class BankCoordinate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true, length = 34)
	private String accountNumber;

	@OneToMany(mappedBy = "bankCoordinate", fetch = FetchType.LAZY)
	private List<BankTransaction> bankTransactions;
}
