package suncertify;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.Util.ProgramMode;
import suncertify.gui.SubContractorController;
import suncertify.gui.MainFrame;
import suncertify.gui.SubContractorModel;
import suncertify.service.ServiceFactory;
import suncertify.service.SubContractorService;

/**
 * The main class of the application. It starts the application mode indicated
 * by the command line argument.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class Main {

	/** The <code>Util</code> instance to access its convenience and helping
	 * methods */
	private static final Util util = Util.getInstance();

	/** The standard logger instance */
	private static final Logger LOGGER = util.getStdLogger();

	/** The ServiceFactory instance */
	private static ServiceFactory servFac = ServiceFactory.getInstance();

	/**
	 * The main method executed at program start.
	 * 
	 * @param args
	 *            the given command line argument(s)
	 */
	public static void main(String[] args) {
		LOGGER.info("Starting application...");
		if (args.length == 0) {
			clientMode();
		} else if (args.length == 1) {
			if ("server".equals(args[0])) {
				serverMode();
			} else if ("alone".equals(args[0])) {
				standaloneMode();
			} else {
				printProgramUsage(args);
			}
		} else {
			printProgramUsage(args);
		}
	}

	/**
	 * Launches the application in network client mode.
	 */
	private static void clientMode() {
		util.setProgramMode(ProgramMode.CLIENT);
		SubContractorService service = servFac.getRemoteClientService();
		launchGui(service);
		LOGGER.info("Client started");
	}

	/**
	 * Launches the GUI part of the application with the specified service.
	 * 
	 * @param service
	 *            the service the GUI should use as data connection
	 */
	private static void launchGui(SubContractorService service) {
		final SubContractorModel model = new SubContractorModel();
		final SubContractorController control = new SubContractorController(
				model, service);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame(model, control);
			}
		});
	}

	/**
	 * Print a little user guide for the program usage indicating the specified
	 * arguments which are wrong and led to a program failure.
	 * 
	 * @param args
	 *            the arguments leading to the program failure
	 */
	private static void printProgramUsage(String[] args) {
		LOGGER.severe("Unsupportet argument list: " + Arrays.deepToString(args)
				+ ". Program shutdown!" + util.newLine()
				+ "Program supports three modes indicated by none ore one "
				+ "parameter (depending on mode)." + util.newLine()
				+ "Available modes are:" + util.newLine()
				+ "server  -  start the server system" + util.newLine()
				+ "alone   -  start the standalone application"
				+ util.newLine() + "<empty> -  start the network client system");
		System.exit(2);
	}

	/**
	 * Launches the application in network server mode.
	 */
	private static void serverMode() {
		util.setProgramMode(ProgramMode.SERVER);
		try {
			LOGGER.info("Starting RMI server...");
			ServiceFactory.getInstance().startRemoteServer();
			LOGGER.info("Server started");
		} catch (Exception e) {
			String message = "The server could not be startet."
					+ util.newLine() + "Program shutdown.";
			LOGGER.log(Level.SEVERE, message, e);
			util.displayErrorMessage(message + util.newLine()
					+ "Please refer to the log for more info.");
		}

	}

	/**
	 * Launches the application in standalone mode.
	 */
	private static void standaloneMode() {
		util.setProgramMode(ProgramMode.STANDALONE);
		SubContractorService service = servFac.getLocalService();
		launchGui(service);
	}

}
