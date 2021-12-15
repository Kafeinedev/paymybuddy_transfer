package com.paymybuddy.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidArgumentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5064924065904933728L;

}
