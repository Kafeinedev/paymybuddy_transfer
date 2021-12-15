package com.paymybuddy.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class WrongUserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5825923170513749928L;

}
