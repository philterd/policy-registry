package ai.philterd.philter.registry.model.exceptions;

/**
 * This exception corresponds to HTTP error 503 Service Unavailable.
 * 
 * @author Philterd, LLC
 *
 */
public final class ServiceUnavailableException extends RuntimeException {

	private static final long serialVersionUID = 8498236096061129077L;
	
	public ServiceUnavailableException(String message) {
		super(message);
	}
	
}
