package suncertify.db;

/**
 * This exception is thrown by the database layer if it tries to access a record
 * which does not exist or is marked as deleted.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class RecordNotFoundException extends Exception {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the exception without any arguments.
	 */
	public RecordNotFoundException() {
		super();
	}

	/**
	 * Constructs the exception with a detail message.
	 * 
	 * @param message
	 *            a detail message about the reason for the exception's throwing
	 */
	public RecordNotFoundException(String message) {
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
	public RecordNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
