package com.paymybuddy.transfer.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "wallets")
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(nullable = false)
	@Builder.Default
	private BigDecimal amount = BigDecimal.ZERO.setScale(2);

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private User owner;

	@OneToMany(mappedBy = "sender")
	private List<WalletLink> outgoingLinks;

	@OneToMany(mappedBy = "receiver")
	private List<WalletLink> incomingLinks;
}
