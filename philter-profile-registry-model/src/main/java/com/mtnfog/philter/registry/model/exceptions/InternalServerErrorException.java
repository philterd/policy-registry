package com.mtnfog.philter.registry.model.exceptions;

/**
 * This exception corresponds to HTTP error 500 Internal Server Error.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public final class InternalServerErrorException extends RuntimeException {

	private static final long serialVersionUID = 8498236096061129077L;
	
	public InternalServerErrorException(String message) {
		super(message);
	}
	
}
