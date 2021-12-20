package com.paymybuddy.transfer.model;

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
 * Connect two wallets between each other. Used in transaction. 
 * The sender wallet is considered the owning wallet and the user owning the wallet also own the walletLink.
 */
@Data
@Entity
@Builder
@Table(name = "wallets_links")
@NoArgsConstructor
@AllArgsConstructor
public class WalletLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, length = 16)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(nullable = false, updatable = false)
	private Wallet sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(nullable = false, updatable = false)
	private Wallet receiver;

	@JsonIgnore
	@OneToMany(mappedBy = "link", fetch = FetchType.LAZY)
	private List<Transaction> transactions;
}
