package gameExceptions;

/**
 * Custom exception class when there is an invalid move played.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.0
 */
public class C4InvalidMoveException extends Exception {

	public C4InvalidMoveException() {

	}

	public C4InvalidMoveException(String message) {
		super(message);
	}

	public C4InvalidMoveException(Throwable cause) {
		super(cause);
	}

	public C4InvalidMoveException(String message, Throwable cause) {
		super(message, cause);
	}

}
