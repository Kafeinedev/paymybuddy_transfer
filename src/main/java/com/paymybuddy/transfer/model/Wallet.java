package com.paymybuddy.transfer.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Owned by a user, keep a record of the current fund of a particular currency available by the user. A user can own multiple wallets.
 */

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(nullable = false, updatable = false)
	private User owner;

	@JsonIgnore
	@OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
	private List<WalletLink> outgoingLinks;

	@JsonIgnore
	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
	private List<WalletLink> incomingLinks;
}
