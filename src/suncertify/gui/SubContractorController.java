package suncertify.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import suncertify.Util;
import suncertify.domain.SubContractor;
import suncertify.domain.SubContractorHandler;
import suncertify.service.SearchCriteria;
import suncertify.service.ServiceFactory;
import suncertify.service.SubContractorAlreadyBookedException;
import suncertify.service.SubContractorNotFoundException;
import suncertify.service.SubContractorService;

/**
 * The <code>SubContractorController</code> is the 'C' in the MVC system, i.e.:
 * It is the central processing element of the GUI.<br>
 * <code>SubContractorController</code> connects the User interface with the
 * <code>SubContractorService</code>. When the user makes an input,
 * <code>SubContractorController</code> gets the inputted data and executes the
 * desired action. If a model data update occurs, the view is notified by
 * <code>fireTableDataChanged()</code>. <br>
 * Furthermore this class fires all change events which are caught and processed
 * by <code>StatusChangeListener</code>.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorController {

	/**
	 * The <code>ElementUpdater</code> is a helper class. Its purpose is to
	 * collect the needed data in a background thread, i.e.: without blocking
	 * the GUI by using the event dispatch thread. The collected elements are
	 * filtered and match a <code>SearchCriteria</code> given in the only
	 * available constructor. <br>
	 * As only <code>SubContractorController</code> uses this class, it is made
	 * unavailable to other classes.
	 * 
	 * @author Jochen R. Meyer
	 * 
	 */
	private class ElementUpdater extends
			SwingWorker<List<SubContractor>, Void> {

		/** The search criteria to use to filter the elements */
		SearchCriteria searchCriteria;

		/**
		 * Constructs the object using the given search criteria for filtering
		 * the elements
		 * 
		 * @param searchCriteria
		 *            the search criteria to use
		 */
		protected ElementUpdater(SearchCriteria searchCriteria) {
			this.searchCriteria = searchCriteria;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected List<SubContractor> doInBackground() throws Exception {
			fireStateChangedRefreshable(false);
			return service.search(searchCriteria);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void done() {
			try {
				model.setElements(get());
				model.fireTableDataChanged();
				int foundCount = model.getElementCount();
				fireStateChangedStatusInfo("Search executed: " + searchCriteria
						+ "  -  " + foundCount
						+ " home improvement contractors found");
				fireStateChangedRefreshable(foundCount > 0);
			} catch (Exception e) {
				if (e instanceof InterruptedException
						|| e instanceof ExecutionException) {
					String message = "Could not fetch element list";
					LOGGER.log(Level.WARNING, message, e);
					message += ": " + e.getCause().getMessage();
					fireStateChangedStatusInfo("");
					UTIL.displayErrorMessage(message);
					fireStateChangedStatusInfo(message);
				} else {
					UTIL.processUnexpectedError(e);
				}
			}
		}
	}

	/** The <code>Util</code> instance to access some convenience and helping
	 * methods */
	private static final Util UTIL = Util.getInstance();

	/** The standard logger instance */
	private static final Logger LOGGER = UTIL.getStdLogger();

	/** The reference to the model */
	private SubContractorModel model;

	/** The reference to the data service */
	private SubContractorService service;

	/** A <code>List</code> of the status change listeners to notify in case of
	 * changes */
	private List<StatusChangeListener> statusChangeListener;

	/** The criteria of the last executed search */
	private SearchCriteria criteriaLastSearch;

	/**
	 * Constructs the object using a reference to the model and a reference to
	 * the service
	 * 
	 * @param model
	 *            the model which data is managed
	 * @param service
	 *            the service providing the data connection
	 */
	public SubContractorController(SubContractorModel model,
			SubContractorService service) {
		this.model = model;
		this.service = service;
		statusChangeListener = new ArrayList<StatusChangeListener>();
		criteriaLastSearch = null;
	}

	/**
	 * Add the specified status change listener. This listener is notified in
	 * case of status changes of the application.
	 * 
	 * @param listener
	 *            the status change listener to add
	 */
	void addApplicationStatusListener(StatusChangeListener listener) {
		statusChangeListener.add(listener);
	}

	/**
	 * Books the specified SubContractor. To obtain the customer ID, a dialog is
	 * displayed in which the user can enter the ID. In case of an exception, an
	 * error dialog appears telling the user what to do to make a valid booking.
	 * 
	 * @param subContractor
	 *            the subcontractor to book
	 */
	void book(SubContractor subContractor) {
		String input = "";
		while (true) {
			input = JOptionPane.showInputDialog(null,
					"Please type in a customer number",
					"Inventory Manager - Input", JOptionPane.PLAIN_MESSAGE);

			if (input != null) {
				try {
					service.book(subContractor, input);
					SubContractor scFromDb = service
							.getSubContractorByRecNo(subContractor.getRecNo());
					model.replaceElement(subContractor, scFromDb);
					model.fireTableDataChanged();
					fireStateChangedStatusInfo("SubContractor booked, "
							+ "table element refreshed");
					return;
				} catch (IllegalArgumentException iae) {
					handleBookException(iae, subContractor.getRecNo());
				} catch (SubContractorAlreadyBookedException sabe) {
					handleBookException(sabe, subContractor.getRecNo());
					return;
				} catch (SubContractorNotFoundException snfe) {
					handleBookException(snfe, subContractor.getRecNo());
					return;
				} catch (Exception e) {
					UTIL.processUnexpectedError(e);
				}
			} else { // Input cancelled by user
				fireStateChangedStatusInfo("Booking cancelled");
				return;
			}
		}

	}

	/**
	 * Uses {@link ServiceFactory#getNewLocalService()} to set a new database
	 * file.
	 */
	void chooseDbFile() {
		SubContractorService service = ServiceFactory.getInstance()
				.getNewLocalService();
		if (service == null) {
			fireStateChangedStatusInfo("Database file selection aborted. "
					+ "Old connection stays active.");
		} else {
			setNewService(service, "Database file changed.");
		}
	}

	/**
	 * Clears the booking of the specified subcontractor.
	 * 
	 * @param subContractor
	 *            the subcontractor whose booking is to clear
	 */
	void clearBooking(SubContractor subContractor) {
		try {
			int reallyClear = JOptionPane.showConfirmDialog(null,
					"Do you really want to clear the booking of '"
							+ subContractor.getName() + "'?",
					"Booking agent - Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (reallyClear == JOptionPane.NO_OPTION) {
				return;
			}
			service.clearBooking(subContractor);
			SubContractor scFromDb = service
					.getSubContractorByRecNo(subContractor.getRecNo());
			model.replaceElement(subContractor, scFromDb);
			model.fireTableDataChanged();
			fireStateChangedStatusInfo("Booking cleared, table element "
					+ "refreshed.");
			return;
		} catch (Exception e) {
			if (e instanceof SubContractorNotFoundException
					|| e instanceof SubContractorAlreadyBookedException) {
				LOGGER.warning("Error while clearing " + "SubContractor "
						+ "booking: " + e.getMessage());
				fireStateChangedStatusInfo("");
				UTIL.displayErrorMessage(e.getMessage());
				fireStateChangedStatusInfo(e.getMessage());
			} else {
				UTIL.processUnexpectedError(e);
			}
		}
	}

	/**
	 * Executes the last done search again, i.e.: make a refresh of the search
	 * before.
	 */
	void executeLastSearch() {
		executeSearch(criteriaLastSearch);
	}

	/**
	 * Executes the search using the given criteria using a background thread so
	 * that the GUI stays responsive.
	 * 
	 * @param searchCriteria
	 *            the searchCriteria to use
	 */
	void executeSearch(SearchCriteria searchCriteria) {
		fireStateChangedStatusInfo("Executing search: " + searchCriteria);
		updateElements(searchCriteria);
		criteriaLastSearch = searchCriteria;
	}

	/**
	 * Notifies all attached <code>StatusChangeListener</code> that there may be
	 * a change whether the specified subcontractor is bookable or not. The new
	 * value is passed to the listener by parameter.
	 * 
	 * @param subContractor
	 *            the subcontractor to check whether it's bookable or not
	 */
	void fireStateChangedBookable(SubContractor subContractor) {
		Boolean bookable;
		if (subContractor == null) {
			bookable = null;
		} else {
			SubContractorHandler scHandler = new SubContractorHandler();
			if (scHandler.isSubContractorBooked(subContractor)) {
				bookable = false;
			} else {
				bookable = true;
			}
		}
		for (StatusChangeListener listener : statusChangeListener) {
			listener.bookableChanged(bookable);
		}
	}

	/**
	 * Notifies all attached <code>StatusChangeListener</code> that the
	 * connection has changed. The new connection is passed to the listener.
	 */
	void fireStateChangedConnection() {
		String newConnection = service.getConnectionAsString();
		for (StatusChangeListener listener : statusChangeListener) {
			listener.connectionChanged(newConnection);
		}
	}

	/**
	 * Notifies all attached <code>StatusChangeListener</code> that possibility
	 * of refreshing may have changed. The new value is passed to the listener
	 * by parameter.
	 * 
	 * @param refreshable
	 *            <code>true</code> if the a refresh is possible,
	 *            <code>false</code> otherwise
	 */
	void fireStateChangedRefreshable(boolean refreshable) {
		for (StatusChangeListener listener : statusChangeListener) {
			listener.refreshableChanged(refreshable);
		}
	}

	/**
	 * Notifies all attached <code>StatusChangeListener</code> that the status
	 * info has changed. The specified status info is passed to the listener.
	 * 
	 * @param newStatusInfo
	 *            the new status info
	 */
	void fireStateChangedStatusInfo(String newStatusInfo) {
		for (StatusChangeListener listener : statusChangeListener) {
			listener.statusInfoChanged(newStatusInfo);
		}
	}

	/**
	 * Uses {@link ServiceFactory#getNewRemoteClientService()} to establish a
	 * connection to a new server.
	 */
	void inputServerAndPort() {
		SubContractorService service = ServiceFactory.getInstance()
				.getNewRemoteClientService();
		if (service == null) {
			fireStateChangedStatusInfo("Server and port input aborted. "
					+ "Old connection stays active.");
		} else {
			setNewService(service, "Server connection changed.");
		}
	}

	/**
	 * Displays a confirmation message if the application shall really be
	 * closed.
	 */
	void reallyExitApplication() {
		int choice = JOptionPane.showConfirmDialog(null,
				"Do you really want to leave the application?",
				"Booking agent - Confirmation", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	/**
	 * Convenience method to set the a new/changed service object specified one.
	 * Furthermore, different changes are published to the attached
	 * <code>StatusChangeListener</code>'s. The new status is set to the
	 * specified value.
	 * 
	 * @param service
	 *            the new service to use for the database connection (RMI server
	 *            or database file)
	 * @param textStatus
	 *            the new status info text
	 */
	void setNewService(SubContractorService service, String textStatus) {
		this.service = service;
		model.clearElements();
		model.fireTableDataChanged();
		fireStateChangedConnection();
		fireStateChangedStatusInfo(textStatus);
		fireStateChangedRefreshable(false);
		
		// Display all available data from database 
		executeSearch(new SearchCriteria());
	}

	/**
	 * Opens a new modal dialog to display the specified subcontractors detail.
	 * 
	 * @param subContractor
	 *            the subcontractor whose detail should be displayed
	 */
	void showSubContractorDetail(SubContractor subContractor) {
		new DetailDialog(subContractor, this);
	}

	/**
	 * Help method to handles a booking exception. it displays an error message
	 * to the user and logs the error. The log is a bit more detailed and has a
	 * more administrative purpose.
	 * 
	 * @param e
	 *            the exception thrown during the booking
	 * @param recNo
	 *            the number of the record in the database
	 */
	private void handleBookException(Exception e, int recNo) {
		fireStateChangedStatusInfo("");
		LOGGER.info("Error while booking SubContractor '" + recNo + "'. "
				+ "Exception message: " + e.getMessage());
		UTIL.displayErrorMessage(e.getMessage());
	}

	/**
	 * Update the model's elements using the given search criteria. The updating
	 * itself is done in a background thread by using
	 * <code>ElementUpdater</code>.
	 * 
	 * @param searchCriteria
	 *            the search criteria to us
	 */
	private void updateElements(SearchCriteria searchCriteria) {
		model.clearElements();
		model.fireTableDataChanged();
		new ElementUpdater(searchCriteria).execute();
	}

}
