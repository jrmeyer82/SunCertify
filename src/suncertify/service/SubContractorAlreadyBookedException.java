package suncertify.service;

/**
 * This exception is thrown if it is tried to book a subcontractor who is
 * already booked.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorAlreadyBookedException extends Exception {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the exception without any arguments.
	 */
	public SubContractorAlreadyBookedException() {
		super();
	}

	/**
	 * Constructs the exception with a detail message.
	 * 
	 * @param message
	 *            a detail message about the reason for the exception's throwing
	 */
	public SubContractorAlreadyBookedException(String message) {
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
	public SubContractorAlreadyBookedException(String message, Throwable cause) {
		super(message, cause);
	}

}
