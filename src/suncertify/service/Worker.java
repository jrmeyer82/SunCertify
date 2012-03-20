package suncertify.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import suncertify.Util;
import suncertify.db.DBMain;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.domain.SubContractor;
import suncertify.domain.SubContractorHandler;

/**
 * The <code>Worker</code> class provides the functionality defined in
 * <code>SubContractorService</code> and <code>SubContractorServer</code>. Their
 * implementing classes use this class when executing the methods. Either by
 * inheritance (is-a: standalone) or by reference (has-a: network server). As
 * the interfaces differ from each other this class implements no interface.
 * 
 * @author Jochen R. Meyer
 * 
 */
class Worker {

	/** A standard error message if a subcontractor cannot be found on the
	 * database. */
	private static final String SC_NOT_FOUND = "Could not find SubContractor "
			+ "in database";

	/** The database file to connecto to */
	protected File dbFile;

	/** The database object to use */
	private DBMain db;

	/** The handler class create <code>SubContractor</code> objects and provide
	 * more information of them. */
	private SubContractorHandler scHandler;

	/**
	 * Constructs the object using the specified database file and initializes
	 * the <code>DBMain</code> implementation instance <code>db</code> with it.
	 * 
	 * @param dbFile
	 *            the database file to connect to
	 */
	protected Worker(File dbFile) {
		try {
			db = new Data(dbFile);
			this.dbFile = dbFile;
			scHandler = new SubContractorHandler();
		} catch (IOException ioe) {
			
			/*
			 * IOException mapped to an IllegalArgumentException to make it
			 * unchecked. This unchecked exceptions is caught and treated
			 * properly in the ServiceFactory class
			 */
			throw new IllegalArgumentException("Cannot use given file '"
					+ dbFile.getAbsolutePath() + "' as database file", ioe);
		}
	}

	/**
	 * Books the specified subcontractor for the specified customer (owner).
	 * 
	 * @param subContractor
	 *            the subcontractor to book
	 * @param customer
	 *            the customer who booked the subcontractor
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 * @throws SubContractorAlreadyBookedException
	 *             if the specified subcontractor is already booked by another
	 *             customer
	 */
	protected void book(SubContractor subContractor, String customer)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		int recNo = subContractor.getRecNo();

		try {

			/*
			 * Lock the record, check if it is bookable (has no customer) and
			 * the given customer value is valid. If yes, update the database.
			 * If no, throw a case-dependent exception.
			 */
			db.lock(recNo);

			SubContractor scFromDb =
					getSubContractorByRecNo(subContractor.getRecNo());

			if (scHandler.isSubContractorBooked(scFromDb)) {
				throw new SubContractorAlreadyBookedException("The "
						+ "SubContractor is already booked by a another "
						+ "customer");
			}

			if (scHandler.isValidCustomer(customer)) {
				scFromDb.setCustomer(customer);
			} else {
				throw new IllegalArgumentException("Please enter a valid "
						+ "customer id, consisting of "
						+ scHandler.getValidCustomerInfo() + "!");
			}

			db.update(recNo, scHandler.transformSubContractorToArray(scFromDb));

		} catch (RecordNotFoundException rnfe) {
			throw new SubContractorNotFoundException(SC_NOT_FOUND, rnfe);
		} finally {
			try {
				
				/*
				 * Try to unlock the record again, no matter if a problem
				 * occurred above
				 */
				db.unlock(subContractor.getRecNo());
			} catch (RecordNotFoundException rnfe) {
				throw new SubContractorNotFoundException(SC_NOT_FOUND, rnfe);
			}
		}
	}

	/**
	 * Clears the booking of the specified subcontractor
	 * 
	 * @param subContractor
	 *            the subcontractor whose booking shall be cleard
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 * @throws SubContractorAlreadyBookedException
	 *             if the specified subcontractor has been booked by another
	 *             customer in the meantime
	 */
	protected void clearBooking(SubContractor subContractor)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		int recNo = subContractor.getRecNo();
		try {
			
			/*
			 * Lock the record and clear it. Don't care if it is already cleared
			 * or not. But throw an exception if the SubContractor's customer
			 * has changed.
			 */
			db.lock(recNo);

			SubContractor scFromDb = 
					getSubContractorByRecNo(subContractor.getRecNo());

			if (scHandler.isSubContractorBooked(scFromDb)
					&& !scFromDb.getCustomer()
							.equals(subContractor.getCustomer())) {
				throw new SubContractorAlreadyBookedException("The "
						+ "SubContractor seems to be booked by another "
						+ "customer now.");
			}

			scFromDb.setCustomer(null);
			db.update(recNo, scHandler.transformSubContractorToArray(scFromDb));
		} catch (RecordNotFoundException rnfe) {
			throw new SubContractorNotFoundException(SC_NOT_FOUND, rnfe);
		} finally {
			try {
				/*
				 * Try to unlock the record again, no matter if a problem
				 * occurred above
				 */
				db.unlock(subContractor.getRecNo());
			} catch (RecordNotFoundException rnfe) {
				throw new SubContractorNotFoundException(SC_NOT_FOUND, rnfe);
			}
		}
	}

	/**
	 * Retrieves the record with the specified record number from the database.
	 * 
	 * @param recNo
	 *            the number of the record to retrieve from the database
	 * @return a <code>SubContractor</code> object representing the database
	 *         record with the specified number
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */
	protected SubContractor getSubContractorByRecNo(int recNo)
			throws SubContractorNotFoundException {
		try {
			String[] data = db.read(recNo);
			return scHandler.createSubContractor(recNo, data);
		} catch (RecordNotFoundException rnfe) {
			throw new SubContractorNotFoundException(SC_NOT_FOUND, rnfe);
		}
	}
	
	/**
	 * Searches in the database for subcontractors matching the criteria
	 * specified in the given argument.
	 * 
	 * @param searchCriteria
	 *            the search criteria specification
	 * @return a <code>List</code> containing all subcontractors matching the
	 *         specified search criteria.
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */
	protected List<SubContractor> search(SearchCriteria searchCriteria)
			throws SubContractorNotFoundException {

		/*
		 * Because the an empty search criteria shall be ignored, the search for
		 * the GUI is a bit more complicated than the search defined by DBMain.
		 * But the inline comments should guide through this method properly.
		 */		
		Util util = Util.getInstance();
		List<SubContractor> resultList = new ArrayList<SubContractor>();		

		// Search for all subcontractors
		if (util.isStringEmptyOrNull(searchCriteria.getName())
				&& util.isStringEmptyOrNull(searchCriteria.getLocation())) {
			return getAllDbElements();

		} else if // Search just for one field
		(util.isStringEmptyOrNull(searchCriteria.getName())
				|| util.isStringEmptyOrNull(searchCriteria.getLocation())) {

			if (util.isStringEmptyOrNull(searchCriteria.getLocation())) {

				// Get field name's matchings
				for (SubContractor sc : getAllDbElements()) {
					if (searchCriteria.getName().equals(sc.getName())) {
						resultList.add(sc);
					}
				}
			} else {

				// Get field location's matchings
				for (SubContractor sc : getAllDbElements()) {
					if (searchCriteria.getLocation().equals(sc.getLocation())) {
						resultList.add(sc);
					}
				}
			}

		} else { // Search for both fields

			if (searchCriteria.isSearchAnd()) {
				for (SubContractor sc : getAllDbElements()) {
					if (searchCriteria.getName().equals(sc.getName())
							&& searchCriteria.getLocation().equals(
									sc.getLocation())) {
						resultList.add(sc);
					}
				}
			} else {
				for (SubContractor sc : getAllDbElements()) {
					if (searchCriteria.getName().equals(sc.getName())
							|| searchCriteria.getLocation().equals(
									sc.getLocation())) {
						resultList.add(sc);
					}
				}
			}

		}

		return resultList;
	}

	/**
	 * Gets all available elements from the db.
	 * 
	 * @return a <code>List</code> of all subcontractors 
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */
	private ArrayList<SubContractor> getAllDbElements()
			throws SubContractorNotFoundException {
		ArrayList<SubContractor> recList = new ArrayList<SubContractor>();
		
		// An empty criteria array means all elements
		String[] empyCriteria  = new SearchCriteria().getCriteria();
		try {
			for (int recNo : db.find(empyCriteria)) {
				recList.add(getSubContractorByRecNo(recNo));
			}
		} catch (RecordNotFoundException rnfe) {
			throw new SubContractorNotFoundException(SC_NOT_FOUND, rnfe);
		}
		return recList;
	}

}
