package com.mtnfog.philter.registry.model.exceptions;

/**
 * This exception corresponds to HTTP exception 400 Bad Request.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public final class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 8498236096061129077L;
	
	public BadRequestException(String message) {
		super(message);
	}
	
}
