package com.paymybuddy.transfer.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true, length = 64)
	private String email;

	@Column(nullable = false, length = 16)
	private String name;

	@Column(nullable = false, columnDefinition = "BINARY(60)")
	private String password;

	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<Wallet> wallets;

	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private List<BankCoordinate> bankCoordinates;
}
