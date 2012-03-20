package suncertify.service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import suncertify.PropertyManager;
import suncertify.Util;
import suncertify.db.Data;

/**
 * The <code>ServiceFactory</code> class is a Singleton and provides data access
 * services. These services implement the <code>SubContractorService</code>
 * interface. <br>
 * While constructing the different services, the user is asked for certain
 * parameters depending on the service in creation. For example: the service for
 * the standalone client needs a database file to be specified. This is done via
 * a file chooser. <br>
 * After finishing the creation the service is used in the GUI as the data
 * connection.
 * 
 * @author Jochen R. Meyer
 * 
 */
public final class ServiceFactory {

	/** The <code>ServiceFactory</code> instance */
	private static ServiceFactory servFac = new ServiceFactory();

	/** The standard value, the RMI server is bind to */
	private static final String RMI_SERVER_NAME = "BookingAgentRMIServer";

	/** The <code>Util</code> instance to access its convenience and helping
	 * methods */
	private static final Util UTIL = Util.getInstance();

	/** The <code>PropertyManager</code> instance to have access to the
	 * application's properties. */
	private static final PropertyManager PROP_MAN = PropertyManager
			.getInstance();

	/** The standard logger instance */
	private static final Logger LOGGER = Util.getInstance().getStdLogger();

	/**
	 * Returns the <code>ServiceFactory</code> instance.
	 * 
	 * @return the <code>ServiceFactory</code> instance
	 */
	public static ServiceFactory getInstance() {
		return servFac;
	}

	/** Constructs the object without any arguments */
	private ServiceFactory() {
	}

	/**
	 * Returns the <code>SubContractorService</code> implementation for the
	 * standalone client. This method is used when starting the application in
	 * standalone mode.
	 * 
	 * @return the <code>SubContractorService</code> implementation for the
	 *         standalone client
	 */
	public SubContractorService getLocalService() {
		File dbFile = getDBFileFromUser(true);
		return new SubContractorServiceLocal(dbFile);
	}

	/**
	 * Returns a newly created <code>SubContractorService</code> implementation
	 * for the standalone client. This method is used when the users changes the
	 * database file.
	 * 
	 * @return the new <code>SubContractorService</code> implementation for the
	 *         standalone client
	 */
	public SubContractorService getNewLocalService() {
		File dbFile = getDBFileFromUser(false);
		if (dbFile == null) { // Input canceled
			return null;
		} else {
			return new SubContractorServiceLocal(dbFile);
		}
	}

	/**
	 * Returns the <code>SubContractorService</code> implementation for the
	 * network client. This method is used when the users changes the server
	 * connection.
	 * 
	 * @return the <code>SubContractorService</code> implementation for the
	 *         network client
	 */
	public SubContractorService getNewRemoteClientService() {
		return getSubContractorServiceClientFromUserInput(false);
	}

	/**
	 * Returns the <code>SubContractorService</code> implementation for the
	 * network client. This method is used when starting the application in
	 * network client mode.
	 * 
	 * @return the <code>SubContractorService</code> implementation for the
	 *         network client
	 */
	public SubContractorService getRemoteClientService() {
		return getSubContractorServiceClientFromUserInput(true);
	}

	/**
	 * Starts the RMI server without a user interface. Only the server port and
	 * the database file must be specified by the user via 2 little dialogs.
	 * 
	 * @throws RemoteException
	 *             if a problem occurs while starting the server
	 */
	public void startRemoteServer() throws RemoteException {
		String serverPort = "";

		InetAddress localHost = null;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException uhe) {
			throw new IllegalAccessError(
					"Could not optain local host information.");
		}

		serverPort = getServerPortFromUser(true);
		File dbFile = getDBFileFromUser(true);

		/*
		 * Try to write the specified properties to the properties file. As it
		 * is done here a cancelled input will have no impact on the properties
		 * file. If the properties could not be written to the properties file,
		 * the user is notified (but a program shutdown is not needed)
		 */
		try {
			PROP_MAN.setProperty(PropertyManager.PROPERTY_SERVER_PORT,
					serverPort);
			PROP_MAN.setProperty(PropertyManager.PROPERTY_DB_FILE_PATH,
					dbFile.getAbsolutePath());
		} catch (IOException ioe) {
			LOGGER.warning("Could not update properties file" + "properly");
		}

		Registry reg = null;

		SubContractorServerImpl server = new SubContractorServerImpl(dbFile);
		SubContractorServer stub = (SubContractorServer) UnicastRemoteObject
				.exportObject(server, 0);
		reg = LocateRegistry.createRegistry(Integer.parseInt(serverPort));
		reg.rebind(RMI_SERVER_NAME, stub);

		LOGGER.info("Sever address : " + localHost.getHostAddress() + " ("
				+ localHost.getHostName() + ")");
		LOGGER.info("Server port   : " + serverPort);
		LOGGER.info("Server name   : " + RMI_SERVER_NAME);
		LOGGER.info("Database file : " + dbFile.getAbsolutePath());
	}

	/**
	 * Displays a <code>JFileChooser</code> in which the user can specify a file
	 * to be used as the application's database. If the input is invalid, the
	 * user is notified and can choose another file. If the user cancels the
	 * input, the application is terminated when the specified parameter is
	 * <code>true</code>. Otherwise <code>null</code> is returned. <br>
	 * The file chooser is preset to the last used database file or a standard
	 * directory (OS dependent) if no last used file is available.
	 * 
	 * @param shutdown
	 *            if <code>true</code>, the application is shut down when the
	 *            user cancels the input; if <code>false</code> the method
	 *            returns <code>null</code>
	 * @return the file, the user specified or <code>null</code>, if the user
	 *         cancelled the input and <code>shutdown</code> is
	 *         <code>false</code>
	 */
	private File getDBFileFromUser(boolean shutdown) {
		
		/*
		 * Load default db file from the properties file. JFileChooser will open
		 * in a standard directory if the loaded file is not available or no
		 * standard file could be loaded
		 */
		File dbFile = new File(
				PROP_MAN.getProperty(PropertyManager.PROPERTY_DB_FILE_PATH));

		JFileChooser fcDBFile = new JFileChooser();
		FileFilter dbFileFilter = new FileNameExtensionFilter("database files",
				"db");
		fcDBFile.addChoosableFileFilter(dbFileFilter);
		fcDBFile.setSelectedFile(dbFile);
		fcDBFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcDBFile.setDialogTitle("Choose a database file");

		/*
		 * Loop until a valid database file is found or the user has cancelled
		 * the input. If shutdown is true, a cancelled input exits the program.
		 */
		while (true) {
			int result = fcDBFile.showDialog(null, "Choose");
			if (result == JFileChooser.APPROVE_OPTION) {
				dbFile = fcDBFile.getSelectedFile();
				if (isValidDBFile(dbFile)) {
					try {
						
						/*
						 * Write the specified properties to the properties
						 * file. As it is done here a cancelled input will have
						 * no impact on the properties file.
						 */
						PROP_MAN.setProperty(
								PropertyManager.PROPERTY_DB_FILE_PATH,
								dbFile.getAbsolutePath());
					} catch (IOException e) {
						LOGGER.warning("Could not update properties file"
								+ "properly");
					} catch (Exception e) {
						UTIL.processUnexpectedError(e);
					}
					return dbFile;
				} else {
					String message = "The specified file '" + dbFile.getName()
							+ "' is not a valid database file. "
							+ UTIL.newLine()
							+ "Please select a valid database file or "
							+ "choose cancel to exit the program.";
					LOGGER.warning(message);
					UTIL.displayErrorMessage(message);
				}
			} else if (result == JFileChooser.CANCEL_OPTION) {
				if (shutdown) {
					LOGGER.severe("Database file input cancelled by user. "
							+ "Program shutdown.");
					System.exit(0);
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * Displays a dialog in which the user must enter the port to be used. If
	 * the input is invalid, the user is notified and can enter the port number
	 * again. If the user cancels the input, the application is terminated when
	 * the specified parameter is <code>true</code>. Otherwise <code>null</code>
	 * is returned. <br>
	 * The input field is preset to the last used value or the standard value
	 * '1099' if no last used value is available.
	 * 
	 * @param shutdown
	 *            if <code>true</code>, the application is shut down when the
	 *            user cancels the input; if <code>false</code> the method
	 *            returns <code>null</code>
	 * @return the port, the user specified or <code>null</code>, if the user
	 *         cancelled the input and <code>shutdown</code> is
	 *         <code>false</code>
	 */
	private String getServerPortFromUser(boolean shutdown) {
		
		/*
		 * Loop until a valid server port is found or the user has cancelled the
		 * input. If shutdown is true, a cancelled input exits the program.
		 */
		while (true) {
			String serverPort = PROP_MAN
					.getProperty(PropertyManager.PROPERTY_SERVER_PORT);
			serverPort = (String) JOptionPane.showInputDialog(null,
					"Please specify the port to be used by the server",
					"Booking Agent - Server configuration",
					JOptionPane.QUESTION_MESSAGE, null, null, serverPort);

			if (serverPort == null) {
				if (shutdown) {
					LOGGER.severe("Server port input cancelled by user. "
							+ "Program shutdown.");
					System.exit(0);
				} else {
					return null;
				}
			}

			try {
				int port = Integer.parseInt(serverPort);
				if ((port < 1024) || (port > 65535)) {
					throw new NumberFormatException();
				}
				return serverPort;
			} catch (NumberFormatException e) {
				String message = "The specified server port '" + serverPort
						+ "' is invalid. "
						+ "Only integers between 1024 and 65535 are valid."
						+ UTIL.newLine()
						+ "Please enter a valid port number or "
						+ "choose cancel to exit the program.";
				LOGGER.warning(message);
				UTIL.displayErrorMessage(message);
			}
		}
	}

	/**
	 * Displays a dialog in which the user must enter the server and port to be
	 * used. If the input is invalid, the user is notified and can enter the
	 * server name and the port number again. If the user cancels the input, the
	 * application is terminated when the specified parameter is
	 * <code>true</code>. Otherwise <code>null</code> is returned. <br>
	 * The input field is preset to the last used values or a placeholder if the
	 * last used value is not available.
	 * 
	 * @param shutdown
	 *            if <code>true</code>, the application is shut down when the
	 *            user cancels the input; if <code>false</code> the method
	 *            returns <code>null</code>
	 * @return a network client <code>SubContractorService</code> implementation
	 *         connected to the RMI server defined by the inputted values or
	 *         <code>null</code>, if the user cancelled the input and
	 *         <code>shutdown</code> is <code>false</code>
	 */
	private SubContractorService getSubContractorServiceClientFromUserInput(
			boolean shutdown) {
		
		/*
		 * Load default server and port from the properties file. A placeholder
		 * string is displayed if no configuration is available.
		 */
		String server = PROP_MAN
				.getProperty(PropertyManager.PROPERTY_SERVER_ADDRESS);
		String port = PROP_MAN
				.getProperty(PropertyManager.PROPERTY_SERVER_PORT);
		String connectionStd = (UTIL.isStringEmptyOrNull(server) ? "<server>"
				: server)
				+ ":"
				+ (UTIL.isStringEmptyOrNull(port) ? "<port>" : port);

		/*
		 * Loop until a valid server connection was inputted or the user has 
		 * cancelled the input. If shutdown is true, a cancelled input exits the
		 * program.
		 */
		while (true) {
			String connection = (String) JOptionPane.showInputDialog(null,
					"Please type in server name or IP address and port. "
							+ UTIL.newLine() + "Usage: <server>:<port>",
					"Booking Agent - Server conntection",
					JOptionPane.QUESTION_MESSAGE, null, null, connectionStd);

			if (connection == null) {
				if (shutdown) {
					LOGGER.severe("Server connection input cancelled by user."
							+ " Program shutdown.");
					System.exit(0);
				} else {
					return null;
				}
			}

			try {
				SubContractorService scs = new SubContractorServiceClient("//"
						+ connection + "/" + RMI_SERVER_NAME);
				
				/*
				 * Write the specified properties to the properties file. As it
				 * is done here a cancelled input will have no impact on the
				 * properties file.
				 */
				String[] connectionElements = connection.split(":");
				PROP_MAN.setProperty(PropertyManager.PROPERTY_SERVER_ADDRESS,
						connectionElements[0]);
				PROP_MAN.setProperty(PropertyManager.PROPERTY_SERVER_PORT,
						connectionElements[1]);
				return scs;
			} catch (RemoteException re) {
				LOGGER.severe("Could not establish connection to '"
						+ connection + "'");
				UTIL.displayErrorMessage("Could not establish connection to '"
						+ connection + "'" + UTIL.newLine()
						+ "Please refer to log for more detail");
			} catch (IOException ioe) {
				LOGGER.warning("Could not update properties file properly");
			} catch (Exception e) {
				UTIL.processUnexpectedError(e);
			}
		}

	}

	/**
	 * Indicates if the specified file is a valid database file.
	 * 
	 * @param dbFile
	 *            the file to be checked
	 * @return <code>true</code> if the specified file is a valid dtabase file,
	 *         <code>false</code> otherwise
	 */
	private boolean isValidDBFile(File dbFile) {
		try {
			new Data(dbFile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
