package suncertify.service;

/**
 * This exception is thrown if a subcontracotr could not be found in the
 * database. <br>
 * Because the service layer should not re-throw exceptions from the database
 * layer, this class is used to encapsulate the database's
 * <code>RecordNotFoundException</code>.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorNotFoundException extends Exception {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the exception without any arguments.
	 */
	public SubContractorNotFoundException() {
		super();
	}

	/**
	 * Constructs the exception with a detail message.
	 * 
	 * @param message
	 *            a detail message about the reason for the exception's throwing
	 */
	public SubContractorNotFoundException(String message) {
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
	public SubContractorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
