package com.paymybuddy.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityMissingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5167448026817312929L;

}
