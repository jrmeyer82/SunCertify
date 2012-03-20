package suncertify;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

/**
 * This singleton class was designed to encapsulate and/or ease up repeating
 * actions.<br>
 * It provides convenience methods as well as helper methods and also some
 * GUI-related elements to make a GUI helper obsolete. It also stores the
 * program mode to make this information available in all classes without
 * passing it around as an argument.
 * 
 * @author Jochen R. Meyer
 * 
 */
public final class Util {

	/**
	 * Enumeration specifying the available program modes.
	 * 
	 * @author Jochen R. Meyer
	 * 
	 */
	public enum ProgramMode {

		/*** Network server mode */
		SERVER(),

		/** Network client mode */
		CLIENT(),

		/** Standalone mode */
		STANDALONE();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String str = super.toString();
			return str.substring(0, 1) + str.substring(1).toLowerCase();
		}
	}

	/** The <code>Util</code> instance */
	private static final Util UTIL = new Util();

	/**
	 * Returns the <code>Util</code> instance.
	 * 
	 * @return the <code>Util</code> instance
	 */
	public static Util getInstance() {
		return UTIL;
	}

	/** The the application's program mode */
	private ProgramMode programMode;

	/**
	 * Constructs the object wiothout any arguments
	 */
	private Util() {

	}

	/**
	 * Displays an error kmessage dialog using the specified message.
	 * 
	 * @param message
	 *            the error message to display
	 */
	public void displayErrorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Booking agent - Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Calculates and returns the window position at which the window has to be
	 * set the be in the center of the screen. The calculation depends on the
	 * specified window size.
	 * 
	 * @param dim
	 *            the dimension of the window
	 * @return the point at which the window has to be set to be in the center
	 *         of the screen
	 */
	public Point getCenteredWindowPosition(Dimension dim) {
		int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth();
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight();
		Point p = new Point();
		p.x = (int) ((screenWidth / 2) - (dim.getHeight() / 2));
		p.y = (int) ((screenHeight / 2) - (dim.getWidth() / 2));
		return p;
	}

	/**
	 * Returns the application's program mode.
	 * 
	 * @return the application's program mode
	 */
	public ProgramMode getProgramMode() {
		if (programMode == null) {
			throw new IllegalAccessError(
					"Program Mode has not yet been initialized.");
		}
		return programMode;
	}

	/**
	 * Returns the application's standard logger.
	 * 
	 * @return the application's standard logger
	 */
	public Logger getStdLogger() {
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.INFO);
		return logger;
	}

	/**
	 * Returns a titled border using the specified string.
	 * 
	 * @param title
	 *            the title of the border
	 * @return a titled border using the specified title
	 */
	public TitledBorder getStdTitledBorder(String title) {
		return new TitledBorder(null, title, TitledBorder.LEADING,
				TitledBorder.TOP, null, Color.GRAY);
	}

	/**
	 * Checks if the specified string is empty or null.
	 * 
	 * @param str
	 *            the string to check
	 * @return <code>true</code> if the specified String is empty or null,
	 *         <code>false</code> otherwise
	 */
	public boolean isStringEmptyOrNull(String str) {
		return str == null || str.equals("");
	}

	/**
	 * Returns a system dependent newline <code>String</code>.
	 * 
	 * @return a system dependent newline <code>String</code>
	 */
	public String newLine() {
		return System.getProperty("line.separator");
	}

	/**
	 * Handles an unexpected error by logging it to the standard logger and
	 * display an error message.
	 * 
	 * @param cause
	 *            the unexpected error to handle
	 */
	public void processUnexpectedError(Throwable cause) {
		String message = "Un enexpected error occurred";
		getStdLogger().log(Level.WARNING, message, cause);
		displayErrorMessage(message + ". Please refer to log "
				+ "for more detail");
	}

	/**
	 * Sets the program mode field to the specified mode.
	 * 
	 * @param programMode
	 *            the application's program mode
	 */
	public void setProgramMode(ProgramMode programMode) {
		this.programMode = programMode;
		UTIL.getStdLogger().info("Application mode : " + programMode);
	}

}
