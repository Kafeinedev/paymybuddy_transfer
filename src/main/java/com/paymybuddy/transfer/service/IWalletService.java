package com.paymybuddy.transfer.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.transfer.exception.EntityMissingException;
import com.paymybuddy.transfer.exception.InvalidArgumentException;
import com.paymybuddy.transfer.model.Wallet;

@Service
public interface IWalletService {

	Wallet createWallet(String ownerEmail, String currency) throws InvalidArgumentException, EntityMissingException;

}