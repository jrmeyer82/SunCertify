package suncertify.db;

/**
 * This exception is thrown by the database layer when trying to create an
 * already present record.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class DuplicateKeyException extends Exception {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the exception without any arguments.
	 */
	public DuplicateKeyException() {
		super();
	}

	/**
	 * Constructs the exception with a detail message.
	 * 
	 * @param message
	 *            a detail message about the reason for the exception's throwing
	 */
	public DuplicateKeyException(String message) {
		super(message);
	}

	/**
	 * Constructs the exception with a detail message and the cause for throwing
	 * it.
	 * 
	 * @param message
	 *            a detail message about the reason for the exception's throwing
	 * @param cause
	 *            the cause for throwing this exception
	 */
	public DuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}

}
