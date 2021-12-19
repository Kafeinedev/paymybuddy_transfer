package com.paymybuddy.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown in case an user tries to effectuate an operation with a
 * wallet that does not hold enough fund for said operation.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientFundException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5006107719547044521L;

}
