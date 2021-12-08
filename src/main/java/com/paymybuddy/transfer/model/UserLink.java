package com.paymybuddy.transfer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "users_links")
@NoArgsConstructor
@AllArgsConstructor
public class UserLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
}
