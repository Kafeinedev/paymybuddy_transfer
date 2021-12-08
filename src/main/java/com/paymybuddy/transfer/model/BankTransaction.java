package com.paymybuddy.transfer.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "bank_transactions")
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	@Builder.Default
	private Date date = new Date();

	@Column(nullable = false, length = 3)
	private String type;

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private BankCoordinate bankCoordinate;

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private Wallet wallet;
}
