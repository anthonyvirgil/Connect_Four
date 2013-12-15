package gameExceptions;

/**
 * Custom exception class when there is an invalid check for a win.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.0
 */
public class C4InvalidCheckWinException extends Exception {

	public C4InvalidCheckWinException() {

	}

	public C4InvalidCheckWinException(String message) {
		super(message);
	}

	public C4InvalidCheckWinException(Throwable cause) {
		super(cause);
	}

	public C4InvalidCheckWinException(String message, Throwable cause) {
		super(message, cause);
	}

}
