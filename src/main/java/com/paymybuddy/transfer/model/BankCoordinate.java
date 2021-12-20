package com.paymybuddy.transfer.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Hold the physical bank account numbers linked to one or multiples users. Also used in bankTransactions.
 */
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

	@JsonIgnore
	@OneToMany(mappedBy = "bankCoordinate", fetch = FetchType.LAZY)
	private List<BankTransaction> bankTransactions;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "users_bank_coordinates", joinColumns = @JoinColumn(name = "bank_coordinate_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users;
}
