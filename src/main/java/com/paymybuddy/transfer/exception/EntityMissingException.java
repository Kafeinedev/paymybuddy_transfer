package com.paymybuddy.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown in case a repository could not find a single entity.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityMissingException extends Exception {

	private static final long serialVersionUID = -5167448026817312929L;

}
