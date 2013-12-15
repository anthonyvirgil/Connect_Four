package gameExceptions;

/**
 * Custom exception class when an invalid message is sent.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.0
 */
public class C4InvalidMessageException extends Exception {

	public C4InvalidMessageException() {

	}

	public C4InvalidMessageException(String message) {
		super(message);
	}

	public C4InvalidMessageException(Throwable cause) {
		super(cause);
	}

	public C4InvalidMessageException(String message, Throwable cause) {
		super(message, cause);
	}

}
