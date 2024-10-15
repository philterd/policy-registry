package ai.philterd.philter.registry.model.exceptions;

/**
 * This exception corresponds to HTTP error 401 Unauthorized.
 * 
 * @author Philterd, LLC
 *
 */
public final class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 8498236096061129077L;
	
	public UnauthorizedException(String message) {
		super(message);
	}
	
}
