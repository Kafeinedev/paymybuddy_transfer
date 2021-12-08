package com.paymybuddy.transfer.model;

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
@Table(name = "wallets_links")
@NoArgsConstructor
@AllArgsConstructor
public class WalletLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, length = 16)
	private String name;

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private Wallet sender;

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private Wallet receiver;

	@OneToMany(mappedBy = "link")
	private List<Transaction> transactions;
}
